#!/usr/bin/env bash
# evidencia-c04-sonar.sh - analisis SonarQube del backend sobre el candidato (C04-04)
# Uso (Git Bash, SonarQube local arriba y PostgreSQL del compose arriba):
#   read -rs SONAR_TOKEN && export SONAR_TOKEN
#   ./scripts/evidencia-c04-sonar.sh <etiqueta>      # p. ej. previo | final
#   unset SONAR_TOKEN
# Escribe evidencia/C04/C04-04_sonar_<etiqueta>.txt (sin token, sin rutas privadas).
set -uo pipefail
cd "$(dirname "$0")/.."
ETIQUETA="${1:-}"; [[ -n "$ETIQUETA" ]] || { echo "Uso: $0 <etiqueta: previo|final>"; exit 2; }
[[ -n "${SONAR_TOKEN:-}" ]] || { echo "Define el token: read -rs SONAR_TOKEN && export SONAR_TOKEN"; exit 2; }
if [[ -f .env ]]; then set -a; source .env; set +a; fi
URL="${SONAR_URL:-http://localhost:9000}"; KEY="mx.uv.tcsw:tcsw-ventas"
SALIDA="evidencia/C04/C04-04_sonar_${ETIQUETA}.txt"; mkdir -p evidencia/C04
api() { curl -s -u "${SONAR_TOKEN}:" "$URL$1"; }

{
echo "== C04-04 SonarQube (${ETIQUETA})  $(date '+%Y-%m-%d %H:%M:%S') =="
echo "commit: $(git rev-parse --short HEAD)  rama: $(git branch --show-current)"
git diff --quiet HEAD -- src pom.xml || echo "AVISO: hay cambios sin commit en src/ o pom.xml; el analisis NO corresponde a un commit exacto"
echo "servidor: $(curl -s "$URL/api/system/status")"
} | tee "$SALIDA"

echo ""; echo "== Analizando (mvn clean verify + sonar) =="
mvn -B clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184:sonar \
  -Dsonar.host.url="$URL" -Dsonar.login="$SONAR_TOKEN" \
  -Dsonar.analysis.commit="$(git rev-parse HEAD)" 2>&1 | grep -E "Tests run:.*Fail|BUILD|ANALYSIS SUCCESSFUL|ERROR\]|WARN.*sonar" | tail -12 | tee -a "$SALIDA"
[[ -f target/sonar/report-task.txt ]] || { echo "NO_VERIFICADO: no se genero el reporte de Sonar" | tee -a "$SALIDA"; exit 1; }

CE="$(grep '^ceTaskUrl=' target/sonar/report-task.txt | cut -d= -f2- | tr -d '\r')"
echo ""; echo "== Esperando el procesamiento en el servidor =="
for _ in $(seq 1 40); do
  ESTADO="$(curl -s -u "${SONAR_TOKEN}:" "$CE" | grep -o '"status":"[A-Z_]*"' | head -1)"
  echo "$ESTADO"; [[ "$ESTADO" == *SUCCESS* || "$ESTADO" == *FAILED* || "$ESTADO" == *CANCELED* ]] && break; sleep 3
done
[[ "$ESTADO" == *SUCCESS* ]] || { echo "NO_VERIFICADO: el servidor no proceso el analisis ($ESTADO)" | tee -a "$SALIDA"; exit 1; }

{
echo ""; echo "== Quality Gate =="
api "/api/qualitygates/project_status?projectKey=${KEY}" | grep -o '"status":"[A-Z]*"' | head -1
echo ""; echo "== Metricas (codigo completo) =="
MED="$(api "/api/measures/component?component=${KEY}&metricKeys=ncloc,bugs,vulnerabilities,code_smells,security_hotspots,coverage,duplicated_lines_density,reliability_rating,security_rating,sqale_rating")"
if echo "$MED" | grep -q '"metric"'; then
  echo "$MED" | tr '{' '\n' | grep '"metric"' | while read -r l; do
    m="$(echo "$l" | sed -nE 's/.*"metric":"([a-z_]+)".*/\1/p')"; v="$(echo "$l" | sed -nE 's/.*"value":"([^"]*)".*/\1/p')"; echo "$m = $v"; done
else
  echo "NO_VERIFICADO: la API de metricas no devolvio datos: $(echo "$MED" | head -c 300)"
fi
echo ""; echo "== Hallazgos abiertos por tipo y severidad =="
api "/api/issues/search?componentKeys=${KEY}&resolved=false&facets=types,severities&ps=1" | grep -oE '"total":[0-9]+' | head -1
api "/api/issues/search?componentKeys=${KEY}&resolved=false&facets=types,severities&ps=1" | grep -oE '"val":"[A-Z_]+","count":[0-9]+'
echo ""; echo "== Hallazgos (regla | severidad | archivo:linea | mensaje) =="
api "/api/issues/search?componentKeys=${KEY}&resolved=false&ps=100" \
  | grep -oE '"rule":"[^"]+"|"severity":"[A-Z]+"|"component":"[^"]+"|"line":[0-9]+|"message":"([^"\\]|\\.)*"' \
  | sed 's/"rule":"//; s/"severity":"//; s/"component":"'"${KEY}"':/ @ /; s/"line":/:/; s/"message":"/ -- /; s/"$//' \
  | paste -sd' ' | sed 's/ \([a-z]*:S[0-9]*\) / \n\1 /g' | head -60
echo ""; echo "RESULTADO: revisa el Quality Gate arriba; el detalle se trata en docs/tratamiento-sonar-c04.md"
} | tee -a "$SALIDA"
