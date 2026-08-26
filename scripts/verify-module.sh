#!/usr/bin/env bash
# verify-module.sh
# Uso: ./scripts/verify-module.sh M01
#      ./scripts/verify-module.sh M02
#
# Comprobacion reproducible de un modulo del curso (P01/M01, P02/M02, ...):
#   1) Registra las versiones efectivas de java, javac y Maven (los tres
#      responden preguntas distintas: runtime invocado, compilador y Java
#      con el que corre el propio proceso de Maven). Cualquier discrepancia
#      queda registrada, no oculta.
#   2) Ejecuta la construccion limpia: mvn clean test.
#   3) Si Maven informa BUILD SUCCESS, imprime MODULO_<MODULO>_VERIFICADO.
#      Si falla, imprime MODULO_<MODULO>_NO_VERIFICADO y conserva el error.
#
# Requiere: JDK y Maven en el PATH, con acceso al repositorio configurado
# (por defecto, Maven Central).

set -euo pipefail

MODULOS_VALIDOS=("M01" "M02")
MODULO="${1:-}"

modulo_valido=false
for m in "${MODULOS_VALIDOS[@]}"; do
  if [[ "$MODULO" == "$m" ]]; then
    modulo_valido=true
    break
  fi
done

if [[ "$modulo_valido" != true ]]; then
  echo "Uso: $0 <${MODULOS_VALIDOS[*]// /|}>"
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
  echo "MODULO_${MODULO}_VERIFICADO"
else
  echo ""
  echo "== Resultado =="
  echo "MODULO_${MODULO}_NO_VERIFICADO"
  echo "La construccion fallo. Conserva este comando y su salida, identifica" >&2
  echo "la causa, corrige y vuelve a ejecutar. No declares MODULO_${MODULO}_VERIFICADO" >&2
  echo "sin haber ejecutado realmente la comprobacion." >&2
  exit 1
fi
