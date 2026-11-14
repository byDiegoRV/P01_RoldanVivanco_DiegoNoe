#!/usr/bin/env bash
# C03-03: evidencia de persistencia de extremo a extremo (API -> PostgreSQL), sin credenciales en la salida.
# Las consultas se ejecutan dentro del contenedor (docker exec) con DB_USER y DB_NAME del .env; no se imprime la contraseña.
#
# Uso (desde cualquier carpeta del repo, con la base y la API en marcha):
#   bash scripts/evidencia-c03-persistencia.sh            # hace una venta y verifica la base
#   bash scripts/evidencia-c03-persistencia.sh reinicio   # tras reiniciar la API: la venta sigue en la base
# Variables opcionales: PORT (9098), PRODUCTO_ID (1), CANTIDAD (1).
cd "$(dirname "$0")/.." || exit 1

BASE="${BASE_URL:-http://localhost:${PORT:-9098}}"
CONT=postgres_ventas
ID="${PRODUCTO_ID:-1}"
CANT="${CANTIDAD:-1}"
MODO="${1:-venta}"
DIR=evidencia/C03
SAL_VENTA="$DIR/C03-03_persistencia.txt"
SAL_REINICIO="$DIR/C03-03_persistencia_reinicio.txt"

if [ -f .env ]; then set -a; . ./.env; set +a; fi
if [ -z "${DB_USER:-}" ] || [ -z "${DB_NAME:-}" ]; then
  echo "Faltan DB_USER y DB_NAME: crea el .env (no se versiona) o cárgalo con: set -a; source .env; set +a" >&2
  exit 1
fi
docker ps --format '{{.Names}}' | grep -qx "$CONT" || { echo "El contenedor $CONT no está en marcha: docker compose up -d" >&2; exit 1; }
curl -s -o /dev/null "$BASE/api/productos" || { echo "No hay API en $BASE: PORT=9098 mvn spring-boot:run" >&2; exit 1; }
mkdir -p "$DIR"

sql() { docker exec "$CONT" psql -U "$DB_USER" -d "$DB_NAME" "$@" | tr -d '\r'; }
val() { sql -tA -c "$1"; }
http() { curl -s -w '\nHTTP %{http_code}\n' "$@"; }
codigo() { printf '%s\n' "$1" | tail -1 | awk '{print $2}'; }
existencia_api() { curl -s "$BASE/api/productos/$ID" | grep -o '"existencia":[0-9]*' | head -1 | cut -d: -f2; }
estado() { if [ "$2" = ok ]; then echo "VERIFICADO: $1"; else echo "NO_VERIFICADO: $1"; fi; }
cmp_ok() { [ "$1" = "$2" ] && echo ok || echo no; }

venta() {
  echo "# C03-03 Persistencia de extremo a extremo (API -> PostgreSQL)"
  echo "API: $BASE | Producto: $ID | Cantidad: $CANT"
  echo
  echo "## 1. Estado inicial en la base"
  sql -c "SELECT id, codigo, nombre, stock FROM productos WHERE id=$ID"
  STOCK0=$(val "SELECT stock FROM productos WHERE id=$ID")
  VENTAS0=$(val "SELECT count(*) FROM ventas")
  DET0=$(val "SELECT count(*) FROM detalle_venta")
  echo "Ventas registradas: $VENTAS0 | Partidas registradas: $DET0"
  case "$STOCK0" in ''|*[!0-9]*) echo "No encontré el producto $ID en la base."; return 1;; esac
  if [ "$STOCK0" -lt "$CANT" ]; then echo "El producto $ID no tiene existencia suficiente: carga datos con scripts/datos-demo.sql o cambia PRODUCTO_ID."; return 1; fi

  echo
  echo "## 2. Venta válida por la API (POST /api/ventas)"
  RESP=$(http -X POST "$BASE/api/ventas" -H 'Content-Type: application/json' -d "{\"partidas\":[{\"productoId\":$ID,\"cantidad\":$CANT}]}")
  echo "$RESP"
  FOLIO=$(printf '%s\n' "$RESP" | grep -o '"folio":"[^"]*"' | head -1 | cut -d'"' -f4)
  echo "Folio registrado: $FOLIO"

  echo
  echo "## 3. La base refleja la venta"
  sql -c "SELECT id, codigo, nombre, stock FROM productos WHERE id=$ID"
  sql -c "SELECT v.folio, v.total, d.producto_id, d.cantidad, d.precio_unitario FROM ventas v JOIN detalle_venta d ON d.venta_id = v.id WHERE v.folio = '$FOLIO'"
  STOCK1=$(val "SELECT stock FROM productos WHERE id=$ID")
  NV=$(val "SELECT count(*) FROM ventas WHERE folio='$FOLIO'")
  ND=$(val "SELECT count(*) FROM detalle_venta d JOIN ventas v ON v.id=d.venta_id WHERE v.folio='$FOLIO'")
  API1=$(existencia_api)
  echo "Existencia en la base: $STOCK0 -> $STOCK1 | Existencia que informa la API: $API1"

  echo
  echo "## 4. Casos negativos: no deben cambiar la base"
  VENTAS1=$(val "SELECT count(*) FROM ventas")
  DET1=$(val "SELECT count(*) FROM detalle_venta")
  echo "### 4a. Existencia insuficiente (cantidad mayor a la existencia)"
  R_A=$(http -X POST "$BASE/api/ventas" -H 'Content-Type: application/json' -d "{\"partidas\":[{\"productoId\":$ID,\"cantidad\":$((STOCK1 + 1000))}]}")
  echo "$R_A"
  echo "### 4b. Producto inexistente"
  R_B=$(http -X POST "$BASE/api/ventas" -H 'Content-Type: application/json' -d '{"partidas":[{"productoId":999999,"cantidad":1}]}')
  echo "$R_B"
  echo "### 4c. Venta sin partidas (vacía)"
  R_C=$(http -X POST "$BASE/api/ventas" -H 'Content-Type: application/json' -d '{"partidas":[]}')
  echo "$R_C"
  VENTAS2=$(val "SELECT count(*) FROM ventas")
  DET2=$(val "SELECT count(*) FROM detalle_venta")
  STOCK2=$(val "SELECT stock FROM productos WHERE id=$ID")
  echo "Después de los tres rechazos: ventas $VENTAS2, partidas $DET2, existencia $STOCK2"

  echo
  echo "## 5. Resultado"
  estado "la venta válida se guardó (una venta y una partida con el folio $FOLIO)" "$([ "$NV" = 1 ] && [ "$ND" = 1 ] && echo ok || echo no)"
  estado "la existencia bajó exactamente $CANT en la base" "$(cmp_ok "$STOCK1" "$((STOCK0 - CANT))")"
  estado "la API y la base informan la misma existencia" "$(cmp_ok "$API1" "$STOCK1")"
  estado "el total de ventas subió en 1 (de $VENTAS0 a $VENTAS1)" "$(cmp_ok "$VENTAS1" "$((VENTAS0 + 1))")"
  C_A=$(codigo "$R_A"); C_B=$(codigo "$R_B"); C_C=$(codigo "$R_C")
  estado "existencia insuficiente rechazada con 409 (obtuvo $C_A)" "$(cmp_ok "$C_A" 409)"
  case "$C_B" in 4??) OKB=ok;; *) OKB=no;; esac
  estado "producto inexistente rechazado con error 4xx (obtuvo $C_B)" "$OKB"
  estado "venta vacía rechazada con 400 (obtuvo $C_C)" "$(cmp_ok "$C_C" 400)"
  estado "los rechazos no cambiaron ventas, partidas ni existencia" "$([ "$VENTAS2" = "$VENTAS1" ] && [ "$DET2" = "$DET1" ] && [ "$STOCK2" = "$STOCK1" ] && echo ok || echo no)"
  echo
  echo "PENDIENTE: reinicia la API y ejecuta: bash scripts/evidencia-c03-persistencia.sh reinicio"
}

reinicio() {
  FOLIO=$(grep -m1 '^Folio registrado: ' "$SAL_VENTA" 2>/dev/null | sed 's/^Folio registrado: //')
  [ -n "$FOLIO" ] || { echo "Primero ejecuta: bash scripts/evidencia-c03-persistencia.sh" >&2; return 1; }
  echo "# C03-03 Persistencia tras reiniciar la API"
  echo "API: $BASE | Folio de la venta anterior: $FOLIO"
  echo
  echo "## La venta sigue en la base"
  sql -c "SELECT v.folio, v.total, d.producto_id, d.cantidad, d.precio_unitario FROM ventas v JOIN detalle_venta d ON d.venta_id = v.id WHERE v.folio = '$FOLIO'"
  NV=$(val "SELECT count(*) FROM ventas WHERE folio='$FOLIO'")
  STOCK=$(val "SELECT stock FROM productos WHERE id=$ID")
  API=$(existencia_api)
  echo
  echo "## La API (ya reiniciada) informa la existencia guardada"
  curl -s "$BASE/api/productos/$ID"; echo
  echo "Existencia en la base: $STOCK | Existencia que informa la API: $API"
  echo
  echo "## Resultado"
  estado "la venta $FOLIO persiste después de reiniciar la API" "$([ "$NV" = 1 ] && echo ok || echo no)"
  estado "la API informa la misma existencia que la base" "$(cmp_ok "$API" "$STOCK")"
}

case "$MODO" in
  venta)    venta    | tee "$SAL_VENTA" ;;
  reinicio) reinicio | tee "$SAL_REINICIO" ;;
  *) echo "Uso: $0 [venta|reinicio]" >&2; exit 1 ;;
esac
