#!/usr/bin/env bash
# verify-stack.sh - verificacion de la pila completa (C04 / M14)
# Uso:  ./scripts/verify-stack.sh              (construye, levanta y verifica)
#       ./scripts/verify-stack.sh --con-caida  (ademas: detiene la BD, comprueba el error y la recuperacion)
#       ./scripts/verify-stack.sh --sin-build  (reutiliza las imagenes ya construidas)
# Ejecutar en Git Bash. Lee .env (no imprime la contrasena).
# Salida: una linea VERIFICADO / NO_VERIFICADO por comprobacion y, al final,
# STACK_C04_VERIFICADO o STACK_C04_NO_VERIFICADO.

set -uo pipefail
cd "$(dirname "$0")/.."

CON_CAIDA=false; BUILD="--build"
for a in "$@"; do
  case "$a" in
    --con-caida) CON_CAIDA=true ;;
    --sin-build) BUILD="" ;;
    *) echo "Argumento desconocido: $a"; exit 2 ;;
  esac
done

if [[ -f .env ]]; then set -a; source .env; set +a; fi
for v in DB_USER DB_PASSWORD DB_NAME DB_PORT; do
  [[ -n "${!v:-}" ]] || { echo "ERROR: falta la variable $v (crea .env desde .env.example)"; exit 2; }
done
WEB_PORT="${WEB_PORT:-9095}"; API_PORT="${API_PORT:-9098}"
WEB="http://localhost:${WEB_PORT}"; API="http://localhost:${API_PORT}"

FALLOS=0
ok()   { echo "VERIFICADO: $1"; }
fail() { echo "NO_VERIFICADO: $1"; FALLOS=$((FALLOS+1)); }
code() { curl -s -o /dev/null -w '%{http_code}' --max-time "${2:-10}" "$1" 2>/dev/null || echo 000; }
esperar_200() {  # url, segundos
  local i; for ((i=0; i<$2; i+=3)); do [[ "$(code "$1")" == "200" ]] && return 0; sleep 3; done; return 1
}
n_bd()  { docker compose exec -T postgres psql -U "$DB_USER" -d "$DB_NAME" -tAc "SELECT count(*) FROM productos" 2>/dev/null | tr -d '\r '; }

echo "== C04 verify-stack  $(date '+%Y-%m-%d %H:%M:%S') =="
echo "-- docker --"
docker --version; docker compose version
docker info >/dev/null 2>&1 || { echo "ERROR: Docker no responde (abre Docker Desktop)"; exit 2; }

echo ""
echo "== [1] Levantando la pila (docker compose up -d $BUILD) =="
if docker compose up -d $BUILD; then ok "docker compose up termino sin error"; else fail "docker compose up fallo"; fi

echo ""
echo "== [2] Esperando servicios (maximo 150 s) =="
if esperar_200 "$API/api/productos" 150; then ok "API responde en $API"; else fail "API no respondio en 150 s"; fi
if esperar_200 "$WEB/" 60; then ok "Frontend (nginx) responde en $WEB"; else fail "Frontend no respondio"; fi

echo ""
echo "== [3] Comprobaciones positivas =="
[[ "$(code "$API/api/productos")" == "200" ]] && ok "GET /api/productos directo a la API = 200" || fail "GET /api/productos directo a la API"
curl -s --max-time 10 "$WEB/" | grep -q "<app-root" && ok "El HTML servido contiene <app-root> (Angular)" || fail "El HTML no contiene <app-root>"
[[ "$(code "$WEB/api/productos")" == "200" ]] && ok "GET /api/productos via nginx (frontend -> API -> BD) = 200" || fail "Proxy nginx -> API"

ANTES="$(n_bd)"
if [[ "$ANTES" == "0" && -f scripts/datos-demo.sql ]]; then
  echo "(BD vacia: se cargan scripts/datos-demo.sql para poder comparar)"
  docker compose exec -T postgres psql -U "$DB_USER" -d "$DB_NAME" -v ON_ERROR_STOP=1 < scripts/datos-demo.sql >/dev/null && ANTES="$(n_bd)"
fi
EN_API="$(curl -s --max-time 10 "$WEB/api/productos" | grep -o '"codigo"' | wc -l | tr -d ' ')"
echo "productos en PostgreSQL: $ANTES | productos que entrega la API via nginx: $EN_API"
if [[ -n "$ANTES" && "$ANTES" != "0" && "$ANTES" == "$EN_API" ]]; then ok "El catalogo de la API coincide con la tabla productos"; else fail "El catalogo de la API no coincide con la BD (o la BD esta vacia)"; fi

echo ""
echo "== [4] Comprobaciones negativas =="
[[ "$(code "$WEB/api/productos/999999")" == "404" ]] && ok "Producto inexistente via nginx = 404" || fail "Producto inexistente no devolvio 404"
[[ "$(code "$WEB/api/no-existe")" == "404" ]] && ok "Ruta inexistente via nginx = 404 (formato unico de error)" || fail "Ruta inexistente no devolvio 404"

if [[ "$CON_CAIDA" == true ]]; then
  echo ""
  echo "== [5] Limite: BD caida y recuperacion =="
  docker compose stop postgres >/dev/null
  C="$(code "$WEB/api/productos" 45)"
  echo "con la BD detenida, GET /api/productos via nginx = $C"
  [[ "$C" != "200" && "$C" != "000" ]] && ok "Con la BD caida la API responde error controlado ($C), no 200" || fail "Con la BD caida se esperaba un error HTTP, se obtuvo $C"
  docker compose start postgres >/dev/null
  if esperar_200 "$WEB/api/productos" 90; then ok "Tras reiniciar la BD el sistema se recupera (200)"; else fail "El sistema no se recupero en 90 s"; fi
fi

echo ""
echo "== Resultado =="
if [[ $FALLOS -eq 0 ]]; then
  echo "STACK_C04_VERIFICADO"
else
  echo "STACK_C04_NO_VERIFICADO ($FALLOS comprobaciones fallidas)"
  echo "-- ultimas lineas del log de la API --"; docker compose logs --tail=25 api 2>/dev/null
  exit 1
fi
