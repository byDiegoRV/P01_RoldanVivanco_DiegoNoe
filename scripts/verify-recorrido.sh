#!/usr/bin/env bash
# verify-recorrido.sh - recorrido vertical de la venta por el cliente web (C04 / M14)
# Cliente (nginx) -> API -> PostgreSQL, con el contrato docs/openapi.yaml como autoridad.
# Requiere la pila arriba (./scripts/verify-stack.sh) y Git Bash.
# Uso: ./scripts/verify-recorrido.sh 2>&1 | tee evidencia/C04/C04-02_recorrido.txt
# Nota: la venta positiva descuenta 1 de existencia de un producto de la BD de pruebas.
# Marca final: RECORRIDO_VERIFICADO o RECORRIDO_NO_VERIFICADO.

set -uo pipefail
cd "$(dirname "$0")/.."
if [[ -f .env ]]; then set -a; source .env; set +a; fi
for v in DB_USER DB_NAME; do [[ -n "${!v:-}" ]] || { echo "ERROR: falta $v (.env)"; exit 2; }; done
WEB="http://localhost:${WEB_PORT:-9095}"

FALLOS=0
ok()   { echo "VERIFICADO: $1"; }
fail() { echo "NO_VERIFICADO: $1"; FALLOS=$((FALLOS+1)); }

sql() { docker compose exec -T postgres psql -U "$DB_USER" -d "$DB_NAME" -tAc "$1" 2>/dev/null | tr -d '\r '; }

STATUS=""; CUERPO=""
llamar() {  # metodo ruta [cuerpo-json]
  local out
  if [[ -n "${3:-}" ]]; then
    out=$(curl -s --max-time 20 -X "$1" "$WEB$2" -H 'Content-Type: application/json' -d "$3" -w $'\n%{http_code}')
  else
    out=$(curl -s --max-time 20 -X "$1" "$WEB$2" -w $'\n%{http_code}')
  fi
  STATUS="${out##*$'\n'}"; CUERPO="${out%$'\n'*}"
}
campo() {  # extrae un valor simple del JSON: campo cadena o numero
  echo "$CUERPO" | sed -n "s/.*\"$1\" *: *\"\{0,1\}\([^\",}]*\).*/\1/p" | head -1
}
sin_fuga() { ! echo "$CUERPO" | grep -qiE 'exception|stacktrace|org\.springframework|at mx\.uv|SQLException|password'; }
# esperar "descripcion" status codigo
esperar() {
  if [[ "$STATUS" == "$2" && "$(campo code)" == "$3" ]] && sin_fuga; then
    ok "$1 -> $2 $3 (sin trazas ni detalles internos)"
  else
    fail "$1 -> se esperaba $2 $3; se obtuvo $STATUS $(campo code)"
  fi
}

echo "== C04 verify-recorrido  $(date '+%Y-%m-%d %H:%M:%S')  cliente: $WEB =="
[[ "$(curl -s -o /dev/null -w '%{http_code}' --max-time 10 "$WEB/api/productos")" == "200" ]] \
  || { echo "ERROR: la pila no responde en $WEB (ejecuta ./scripts/verify-stack.sh)"; echo "RECORRIDO_NO_VERIFICADO"; exit 1; }

# Producto con existencia para la venta positiva (se toma de la BD, no se supone un id)
PID="$(sql "SELECT id FROM productos WHERE stock >= 2 ORDER BY id LIMIT 1")"
[[ -n "$PID" ]] || { echo "ERROR: no hay productos con existencia >= 2 (carga scripts/datos-demo.sql)"; echo "RECORRIDO_NO_VERIFICADO"; exit 1; }
echo "Producto de prueba: id=$PID"

echo ""
echo "== [A] Positivo: la venta recorre cliente -> API -> PostgreSQL =="
llamar GET "/api/productos/$PID"
EXIST_API="$(campo existencia)"; STOCK_ANTES="$(sql "SELECT stock FROM productos WHERE id=$PID")"
if [[ "$STATUS" == "200" && "$EXIST_API" == "$STOCK_ANTES" ]]; then
  ok "GET producto = 200 y existencia de la API ($EXIST_API) = stock en PostgreSQL ($STOCK_ANTES) [contrato: existencia]"
else
  fail "GET producto: status $STATUS, existencia API '$EXIST_API' vs BD '$STOCK_ANTES'"
fi

VENTAS_ANTES="$(sql "SELECT count(*) FROM ventas")"
llamar POST /api/ventas "{\"partidas\":[{\"productoId\":$PID,\"cantidad\":1}]}"
FOLIO="$(campo folio)"
if [[ "$STATUS" == "201" && -n "$FOLIO" ]]; then ok "POST /api/ventas = 201 con folio generado por el servidor ($FOLIO)"; else fail "POST venta valida: status $STATUS, folio '$FOLIO'"; fi

if [[ -n "$FOLIO" ]]; then
  [[ "$(sql "SELECT count(*) FROM ventas WHERE folio='$FOLIO'")" == "1" ]] && ok "PostgreSQL: existe 1 fila en ventas con ese folio" || fail "PostgreSQL: no existe la venta $FOLIO"
  [[ "$(sql "SELECT count(*) FROM detalle_venta d JOIN ventas v ON v.id=d.venta_id WHERE v.folio='$FOLIO' AND d.producto_id=$PID AND d.cantidad=1")" == "1" ]] \
    && ok "PostgreSQL: detalle_venta tiene la partida (producto $PID, cantidad 1)" || fail "PostgreSQL: falta la partida en detalle_venta"
fi
STOCK_DESPUES="$(sql "SELECT stock FROM productos WHERE id=$PID")"
[[ "$STOCK_DESPUES" == "$((STOCK_ANTES-1))" ]] && ok "PostgreSQL: existencia $STOCK_ANTES -> $STOCK_DESPUES (descuento aplicado)" || fail "Existencia esperada $((STOCK_ANTES-1)), actual $STOCK_DESPUES"

llamar GET "/api/productos/$PID"
[[ "$(campo existencia)" == "$STOCK_DESPUES" ]] && ok "Retest vertical: el catalogo (API via nginx) ya muestra la existencia nueva" || fail "El catalogo no refleja la existencia nueva"

echo ""
echo "== [B] Negativos del contrato (todos via nginx) y atomicidad =="
VENTAS_BASE="$(sql "SELECT count(*) FROM ventas")"; STOCK_BASE="$(sql "SELECT stock FROM productos WHERE id=$PID")"

llamar POST /api/ventas "{\"partidas\":[{\"productoId\":$PID,\"cantidad\":999999}]}"
esperar "Existencia insuficiente" 409 EXISTENCIA_INSUFICIENTE
llamar POST /api/ventas "{\"partidas\":[{\"productoId\":$PID,\"cantidad\":0}]}"
esperar "Cantidad invalida" 400 VALIDACION_FALLIDA
llamar POST /api/ventas '{"partidas":[{"productoId":999999,"cantidad":1}]}'
esperar "Producto inexistente en la venta" 404 PRODUCTO_NO_ENCONTRADO
llamar POST /api/ventas "{\"tarjeta\":\"4111111111111111\",\"partidas\":[{\"productoId\":$PID,\"cantidad\":1}]}"
esperar "Campo no permitido (datos de tarjeta)" 400 SOLICITUD_INVALIDA
llamar POST /api/ventas '{"partidas": ['
esperar "JSON mal formado" 400 SOLICITUD_INVALIDA
llamar GET /api/productos/abc
esperar "Id no numerico" 400 SOLICITUD_INVALIDA
llamar GET /api/productos/999999
esperar "Producto inexistente" 404 PRODUCTO_NO_ENCONTRADO
llamar DELETE /api/productos
esperar "Metodo no permitido" 405 METODO_NO_PERMITIDO
llamar GET /api/no-existe
esperar "Ruta inexistente" 404 RECURSO_NO_ENCONTRADO

if [[ "$(sql "SELECT count(*) FROM ventas")" == "$VENTAS_BASE" && "$(sql "SELECT stock FROM productos WHERE id=$PID")" == "$STOCK_BASE" ]]; then
  ok "Atomicidad: las peticiones rechazadas no crearon ventas ni cambiaron existencias en PostgreSQL"
else
  fail "Atomicidad: una peticion rechazada modifico la BD"
fi

echo ""
echo "== Resultado =="
if [[ $FALLOS -eq 0 ]]; then echo "RECORRIDO_VERIFICADO"; else echo "RECORRIDO_NO_VERIFICADO ($FALLOS comprobaciones fallidas)"; exit 1; fi
