#!/usr/bin/env bash
# aplicar-readme-c04.sh - actualiza README.md para el candidato v1.0-rc (C04-08)
#  - corrige las menciones al Dockerfile de P01 (tabla y arbol de archivos, seccion 4)
#  - inserta la seccion "Candidato de liberacion v1.0-rc" despues de la presentacion
# Ejecutar una sola vez, desde la raiz del repositorio. No usa anclas de fin de linea (el README puede tener CRLF).
set -euo pipefail
cd "$(dirname "$0")/.."
PARTE="docs/_readme_c04.part"
[[ -f README.md && -f "$PARTE" ]] || { echo "ERROR: falta README.md o $PARTE"; exit 2; }
grep -q "Candidato de liberación v1.0-rc" README.md && { echo "El README ya tiene la seccion C04: no se aplica de nuevo"; exit 0; }

# 1) menciones historicas al Dockerfile de P01
sed -i 's/^├── Dockerfile/├── Dockerfile.api, Dockerfile.frontend y docker-compose.yml  (C04; sustituyen al Dockerfile de P01)/' README.md
sed -i 's/^Dockerfile\(\t\| \+\)Ambiente reproducible mediante contenedor/Dockerfile.api, Dockerfile.frontend, docker-compose.yml\1Ambiente reproducible en contenedores (C04; sustituyen al Dockerfile de P01)/' README.md
sed -i '/^4\. Alternativa reproducible con Docker/a\> **Nota (C04):** esta sección describe el Dockerfile de P01, que fue sustituido por `Dockerfile.api`, `Dockerfile.frontend` y `docker-compose.yml`. Para construir y verificar la pila actual use la sección "Candidato de liberación v1.0-rc".' README.md
sed -i '/^El Dockerfile se entrega como ambiente reproducible adicional/a\Nota (C04): el Dockerfile de P01 fue sustituido y la pila en contenedores se verificó en C04 (`MODULO_M14_VERIFICADO`).' README.md

# 2) seccion nueva despues de la linea del ultimo punto de la presentacion (P03 - Modulo M03)
LINEA="$(grep -n '^•.*P03 — Módulo M03' README.md | head -1 | cut -d: -f1 || true)"
[[ -n "$LINEA" ]] || { echo "ERROR: no encontre la linea de P03 en la presentacion; no se inserto la seccion"; exit 1; }
sed -i "${LINEA}r ${PARTE}" README.md
rm -f "$PARTE"

echo "== Comprobacion =="
grep -c "Candidato de liberación v1.0-rc" README.md | sed 's/^/secciones C04 (esperado 1 o mas): /'
grep -n "Dockerfile" README.md | cut -c1-110
