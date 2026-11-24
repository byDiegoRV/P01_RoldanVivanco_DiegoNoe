#!/usr/bin/env bash
# ensayo-reversa.sh - ensayo real del plan de reversa v1.0-rc -> v0.3-web -> v1.0-rc (C04-06)
# Que hace (ver docs/plan-reversa.md):
#   [1] estado del candidato y respaldo logico (pg_dump) + huella de los datos
#   [2] baja la pila del candidato (sin borrar su volumen)
#   [3] arma una copia del tag v0.3-web (git worktree) y levanta ESA version con proyecto Compose aparte
#   [4] restaura el respaldo y compara la huella de los datos
#   [5] verifica la version anterior (pila + prueba de que sirve otro frontend)
#   [6] regresa al candidato y verifica que sigue sano y con los mismos datos
# Uso (Git Bash, en la raiz, rama del candidato con todo commiteado, pila del candidato arriba):
#   ./scripts/ensayo-reversa.sh 2>&1 | tee evidencia/C04/C04-06_ensayo_reversa.txt
# Marca final: ROLLBACK_VERIFICADO o ROLLBACK_NO_VERIFICADO. Tarda 10-15 min (reconstruye imagenes).
set -uo pipefail
cd "$(dirname "$0")/.."
ROOT="$(pwd)"
TAG="v0.3-web"
WT="$(cd .. && pwd)/tcsw-ventas-rollback"
RPROY="tcsw-rollback"
if [[ -f .env ]]; then set -a; source .env; set +a; fi
for v in DB_USER DB_PASSWORD DB_NAME DB_PORT; do [[ -n "${!v:-}" ]] || { echo "ERROR: falta $v en .env"; exit 2; }; done
WEB="http://localhost:${WEB_PORT:-8080}"; API="http://localhost:${API_PORT:-9098}"
FALLOS=0
ok()   { echo "VERIFICADO: $1"; }
fail() { echo "NO_VERIFICADO: $1"; FALLOS=$((FALLOS+1)); }
t0=$SECONDS; marca() { echo "[t+$((SECONDS-t0))s] $1"; }
q() { docker compose exec -T postgres psql -U "$DB_USER" -d "$DB_NAME" -tAc "$1" 2>/dev/null | tr -d '\r '; }
huella() {
  for t in productos ventas detalle_venta; do
    echo "$t: filas=$(q "SELECT count(*) FROM $t") md5=$(q "SELECT coalesce(md5(string_agg(x::text, ',' ORDER BY x::text)),'vacia') FROM $t x")"
  done
}
esperar_200() { local i; for ((i=0; i<$2; i+=3)); do [[ "$(curl -s -o /dev/null -w '%{http_code}' --max-time 10 "$1")" == "200" ]] && return 0; sleep 3; done; return 1; }
esperar_pg()  { local i; for ((i=0; i<90; i+=3)); do docker compose exec -T postgres pg_isready -h 127.0.0.1 -U "$DB_USER" -d "$DB_NAME" >/dev/null 2>&1 && return 0; sleep 3; done; return 1; }
css() { curl -s --max-time 10 "$WEB/" | grep -o 'styles[^"]*\.css' | head -1; }

echo "== C04-06 Ensayo de reversa  $(date '+%Y-%m-%d %H:%M:%S') =="
RC_SHA="$(git rev-parse --short HEAD)"; echo "candidato: rama $(git rev-parse --abbrev-ref HEAD) @ $RC_SHA"
echo "destino de reversa: $TAG @ $(git rev-parse --short $TAG^{commit})"
[[ -n "$(git status --porcelain --untracked-files=no)" ]] && echo "AVISO: hay cambios sin commit; el ensayo usa los archivos de infraestructura del arbol de trabajo"
docker info >/dev/null 2>&1 || { echo "ERROR: Docker no responde"; exit 2; }
[[ -e "$WT" ]] && { echo "ERROR: ya existe $WT (borralo: git worktree remove --force ../tcsw-ventas-rollback)"; exit 2; }

echo ""; echo "== [1] Estado del candidato y respaldo =="; marca "inicio"
docker compose up -d --build >/dev/null 2>&1; docker compose restart web >/dev/null 2>&1
esperar_200 "$API/api/productos" 60 && ok "el candidato responde antes de empezar" || fail "el candidato no responde (levanta la pila primero)"
CSS_RC="$(css)"; echo "frontend del candidato sirve: ${CSS_RC:-?}"
H_ANTES="$(huella)"; echo "$H_ANTES"
mkdir -p backups
RESP="backups/respaldo_$(date +%Y%m%d_%H%M%S).sql"
if docker compose exec -T postgres pg_dump -U "$DB_USER" -d "$DB_NAME" --clean --if-exists > "$RESP" 2>/dev/null && [[ -s "$RESP" ]]; then
  ok "respaldo logico generado ($(wc -c < "$RESP") bytes, fuera del repositorio: backups/)"; else fail "no se pudo generar el respaldo"; fi

echo ""; echo "== [2] Bajando el candidato (el volumen de datos se conserva) =="; marca "down candidato"
docker compose down >/dev/null 2>&1 && ok "pila del candidato detenida (sin -v)" || fail "no se pudo bajar el candidato"

echo ""; echo "== [3] Levantando $TAG =="; marca "worktree"
if git worktree add --detach "$WT" "$TAG" >/dev/null 2>&1; then ok "copia del tag en $(basename "$WT")"; else fail "git worktree add $TAG"; fi
cp .env "$WT/.env"
# La definicion de despliegue (Compose + Dockerfiles) es la del candidato; el CODIGO es el del tag
for f in docker-compose.yml Dockerfile.api Dockerfile.frontend frontend/nginx.conf .dockerignore frontend/.dockerignore scripts/verify-stack.sh scripts/verify-recorrido.sh; do
  [[ -e "$f" ]] && cp "$f" "$WT/$f"
done
echo "version en la copia: $(git -C "$WT" describe --tags --exact-match 2>/dev/null)"
cd "$WT"; export COMPOSE_PROJECT_NAME="$RPROY"
docker compose up -d postgres >/dev/null 2>&1
esperar_pg && ok "PostgreSQL de la version anterior listo (volumen nuevo y vacio)" || fail "PostgreSQL no arranco"

echo ""; echo "== [4] Restaurando el respaldo =="; marca "restore"
if docker compose exec -T postgres psql -U "$DB_USER" -d "$DB_NAME" -v ON_ERROR_STOP=1 -q < "$ROOT/$RESP" >/dev/null 2>"$ROOT/backups/restore_err.txt"; then ok "respaldo restaurado"; else fail "la restauracion fallo (ver backups/restore_err.txt)"; fi
H_REST="$(huella)"; echo "$H_REST"
[[ "$H_REST" == "$H_ANTES" ]] && ok "los datos restaurados coinciden con los del candidato (filas y md5 por tabla)" || fail "los datos restaurados NO coinciden con los del candidato"

echo ""; echo "== [5] Verificando $TAG =="; marca "verify-stack"
./scripts/verify-stack.sh 2>&1 | tail -n 25 > "$ROOT/backups/stack_anterior.txt"; cat "$ROOT/backups/stack_anterior.txt"
grep -q '^STACK_C04_VERIFICADO' "$ROOT/backups/stack_anterior.txt" && ok "la version anterior pasa la verificacion de pila" || fail "la version anterior no paso verify-stack"
CSS_OLD="$(css)"; echo "frontend de $TAG sirve: ${CSS_OLD:-?}"
[[ -n "$CSS_OLD" && "$CSS_OLD" != "$CSS_RC" ]] && ok "se sirve otro frontend ($CSS_OLD) distinto al del candidato ($CSS_RC): la reversa realmente cambio de version" || fail "el frontend servido es el mismo que el del candidato; no hay prueba de cambio de version"
H_OLD="$(huella)"; [[ "$H_OLD" == "$H_ANTES" ]] && ok "datos intactos en la version anterior" || fail "los datos cambiaron durante la verificacion de la version anterior"

echo ""; echo "== [6] Regresando al candidato =="; marca "down rollback"
docker compose down -v >/dev/null 2>&1 && ok "pila de $TAG eliminada (proyecto $RPROY, con su volumen de ensayo)" || fail "no se pudo bajar la pila de ensayo"
cd "$ROOT"; unset COMPOSE_PROJECT_NAME
git worktree remove --force "$WT" >/dev/null 2>&1 && ok "copia del tag eliminada" || fail "no se pudo quitar el worktree"
docker compose up -d --build >/dev/null 2>&1 || fail "docker compose up del candidato"
esperar_200 "$API/api/productos" 150 && ok "el candidato volvio a responder" || fail "el candidato no volvio"
docker compose restart web >/dev/null 2>&1; sleep 5
CSS_BACK="$(css)"; [[ "$CSS_BACK" == "$CSS_RC" ]] && ok "se sirve de nuevo el frontend del candidato ($CSS_BACK)" || fail "el frontend servido no es el del candidato ($CSS_BACK)"
H_BACK="$(huella)"; [[ "$H_BACK" == "$H_ANTES" ]] && ok "datos del candidato intactos tras el ensayo" || fail "datos del candidato distintos tras el ensayo"
./scripts/verify-recorrido.sh 2>&1 | tail -n 3
marca "fin"

echo ""; echo "== Resultado =="
if [[ $FALLOS -eq 0 ]]; then echo "ROLLBACK_VERIFICADO (duracion total: $((SECONDS-t0)) s)"; else echo "ROLLBACK_NO_VERIFICADO ($FALLOS comprobaciones fallidas)"; exit 1; fi
