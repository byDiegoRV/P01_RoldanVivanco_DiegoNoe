#!/usr/bin/env bash
# verify-module.sh
# Uso: ./scripts/verify-module.sh M01
#
# Verifica la linea base del modulo M01 (P01. Ambiente reproducible y Producto):
#   1) Confirma version de Java y Maven.
#   2) Compila el proyecto.
#   3) Ejecuta las pruebas automatizadas (JUnit 5) sobre la clase Producto.
#
# Requiere: Java 11+ y Maven 3.6+ en el PATH, con acceso al repositorio
# configurado en settings.xml (por defecto, Maven Central).

set -euo pipefail

MODULO="${1:-}"
if [[ "$MODULO" != "M01" ]]; then
  echo "Uso: $0 M01"
  exit 1
fi

cd "$(dirname "$0")/.."

echo "== Version de Java =="
java -version

echo ""
echo "== Version de Maven =="
mvn -version

echo ""
echo "== Compilando y probando (mvn clean test) =="
mvn -B clean test

echo ""
echo "== Verificacion M01 completada sin errores =="
