#!/usr/bin/env bash
# verify-module.sh
# Uso: ./scripts/verify-module.sh M01
#
# Comprobacion reproducible del modulo M01 (P01. Ambiente reproducible y
# Producto), segun M01_lectura_estudiante:
#   1) Registra las versiones efectivas de java, javac y Maven (los tres
#      responden preguntas distintas: runtime invocado, compilador y Java
#      con el que corre el propio proceso de Maven). Cualquier discrepancia
#      queda registrada, no oculta.
#   2) Ejecuta la construccion limpia: mvn clean test.
#   3) Si Maven informa BUILD SUCCESS, imprime MODULO_M01_VERIFICADO.
#
# Perfil de ejecucion declarado para P01: Java 11 y Maven 3.9.9.
# Requiere: Java 11 (o superior compatible con --release 11) y Maven en el
# PATH, con acceso al repositorio configurado (por defecto, Maven Central).

set -euo pipefail

MODULO="${1:-}"
if [[ "$MODULO" != "M01" ]]; then
  echo "Uso: $0 M01"
  exit 1
fi

cd "$(dirname "$0")/.."

echo "== Registro de versiones efectivas =="
echo "-- java (runtime) --"
java -version
echo ""
echo "-- javac (compilador) --"
javac -version
echo ""
echo "-- mvn (Java con el que corre Maven) --"
mvn -version

echo ""
echo "== Compilando y probando (mvn clean test) =="
if mvn -B clean test; then
  echo ""
  echo "== Resultado =="
  echo "MODULO_M01_VERIFICADO"
else
  echo ""
  echo "== Resultado =="
  echo "MODULO_M01_NO_VERIFICADO"
  echo "La construccion fallo. Conserva este comando y su salida, identifica" >&2
  echo "la causa, corrige y vuelve a ejecutar. No declares MODULO_M01_VERIFICADO" >&2
  echo "sin haber ejecutado realmente la comprobacion." >&2
  exit 1
fi
