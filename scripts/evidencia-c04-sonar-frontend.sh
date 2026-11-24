#!/usr/bin/env bash
# evidencia-c04-sonar-frontend.sh - analisis SonarQube del cliente Angular/TypeScript (C04-04)
# Usa el escaner oficial en Docker (trae su propio Node 18, no depende del Node local).
# Proyecto aparte en SonarQube: mx.uv.tcsw:tcsw-ventas-frontend (no mezcla con el backend).
# Uso: read -rs SONAR_TOKEN && export SONAR_TOKEN; ./scripts/evidencia-c04-sonar-frontend.sh; unset SONAR_TOKEN
#
# Dos decisiones que salen del diagnostico (docs/tratamiento-sonar-c04.md, seccion 3):
#  1) -Dsonar.working.directory=/tmp/scannerwork: el escaner despliega su paquete de analisis
#     (miles de archivos) en el sistema de archivos del contenedor; sobre el montaje de Windows
#     el analisis se quedaba detenido en "Deploying eslint-bridge".
#  2) Se analiza una copia ligera (src + configuracion, sin node_modules): menos tipos resueltos
#     que con dependencias instaladas, limite declarado.
set -uo pipefail
cd "$(dirname "$0")/.."
[[ -n "${SONAR_TOKEN:-}" ]] || { echo "Define el token: read -rs SONAR_TOKEN && export SONAR_TOKEN"; exit 2; }
SALIDA="evidencia/C04/C04-04_sonar_frontend.txt"; mkdir -p evidencia/C04
COPIA="$(mktemp -d)"
cp -r frontend/src frontend/package.json frontend/tsconfig*.json frontend/angular.json "$COPIA/"
RAIZ="$(cd "$COPIA" && (pwd -W 2>/dev/null || pwd))"
{
echo "== C04-04 SonarQube frontend  $(date '+%Y-%m-%d %H:%M:%S') =="
echo "commit: $(git rev-parse --short HEAD)"
MSYS_NO_PATHCONV=1 timeout 600 docker run --rm \
  -e SONAR_HOST_URL="http://host.docker.internal:9000" -e SONAR_TOKEN \
  -v "${RAIZ}:/usr/src" \
  sonarsource/sonar-scanner-cli:5.0.1 \
  -Dsonar.working.directory=/tmp/scannerwork \
  -Dsonar.projectKey=mx.uv.tcsw:tcsw-ventas-frontend \
  -Dsonar.projectName=tcsw-ventas-frontend \
  -Dsonar.sources=src -Dsonar.exclusions='**/*.spec.ts' \
  -Dsonar.sourceEncoding=UTF-8 2>&1 | grep -E "INFO: (Sensor|[0-9]+ source files|Analysis total|ANALYSIS|EXECUTION)|ERROR|WARN.*(tsconfig|node)" 
echo "codigo del escaner: ${PIPESTATUS[0]}"
} | tee "$SALIDA"
rm -rf "$COPIA"
