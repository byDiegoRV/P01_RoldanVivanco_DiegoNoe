#!/usr/bin/env bash
# verify-persistencia.sh - recorrido de la venta con las dos variantes de persistencia (C04-05)
# La API elige el adaptador con la variable PERSISTENCIA (hibernate | jdbc, ver ConfiguracionApi).
# Para cada modo: recrea el contenedor de la API con esa variable, comprueba que la variable llego,
# cuenta las lineas de arranque de Hibernate en el log (informativo) y ejecuta verify-recorrido.sh.
# Uso (pila arriba): ./scripts/verify-persistencia.sh 2>&1 | tee evidencia/C04/C04-05b_persistencia_modos.txt
# Marca final: PERSISTENCIA_VERIFICADA o PERSISTENCIA_NO_VERIFICADA.
set -uo pipefail
cd "$(dirname "$0")/.."
if [[ -f .env ]]; then set -a; source .env; set +a; fi
API="http://localhost:${API_PORT:-9098}"
FALLOS=0
ok()   { echo "VERIFICADO: $1"; }
fail() { echo "NO_VERIFICADO: $1"; FALLOS=$((FALLOS+1)); }
esperar_api() { local i; for ((i=0; i<150; i+=3)); do
  [[ "$(curl -s -o /dev/null -w '%{http_code}' --max-time 10 "$API/api/productos")" == "200" ]] && return 0; sleep 3; done; return 1; }

echo "== C04-05b Persistencia por modo  $(date '+%Y-%m-%d %H:%M:%S') =="
echo "commit: $(git rev-parse --short HEAD)"
LOG="$(mktemp)"
for modo in hibernate jdbc; do
  echo ""; echo "=================== PERSISTENCIA=$modo ==================="
  export PERSISTENCIA="$modo"
  docker compose up -d --force-recreate api >/dev/null 2>&1
  # nginx resuelve el nombre "api" al arrancar: se reinicia el cliente para que apunte al contenedor nuevo
  docker compose restart web >/dev/null 2>&1
  if esperar_api; then ok "API arriba con PERSISTENCIA=$modo"; else fail "la API no respondio con PERSISTENCIA=$modo"; continue; fi
  VAR="$(docker compose exec -T api env 2>/dev/null | tr -d '\r' | grep '^PERSISTENCIA=')"
  [[ "$VAR" == "PERSISTENCIA=$modo" ]] && ok "la variable llego al contenedor ($VAR)" || fail "la variable no llego al contenedor (se vio: '$VAR')"
  HHH="$(docker compose logs api 2>/dev/null | grep -c 'HHH')"
  echo "informativo: lineas de arranque de Hibernate (HHH...) en el log de la API: $HHH"
  sleep 5
  ./scripts/verify-recorrido.sh 2>&1 | tee "$LOG"
  grep -q '^RECORRIDO_VERIFICADO' "$LOG" && ok "recorrido completo con PERSISTENCIA=$modo" || fail "recorrido con PERSISTENCIA=$modo"
done
rm -f "$LOG"

echo ""; echo "== Restaurando la pila al modo por defecto (hibernate) =="
unset PERSISTENCIA
docker compose up -d --force-recreate api >/dev/null 2>&1
docker compose restart web >/dev/null 2>&1
esperar_api && ok "pila restaurada" || fail "no se pudo restaurar la pila"

echo ""; echo "== Resultado =="
if [[ $FALLOS -eq 0 ]]; then echo "PERSISTENCIA_VERIFICADA"; else echo "PERSISTENCIA_NO_VERIFICADA ($FALLOS comprobaciones fallidas)"; exit 1; fi
