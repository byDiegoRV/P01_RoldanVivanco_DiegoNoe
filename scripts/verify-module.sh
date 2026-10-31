#!/usr/bin/env bash
# verify-module.sh
# Uso: ./scripts/verify-module.sh M01
#      ./scripts/verify-module.sh M02
#      ./scripts/verify-module.sh M11   (requiere Docker y variables DB_*)
#
# Comprobacion reproducible de un modulo del curso (P01/M01, P02/M02, ...):
#   1) Registra las versiones efectivas de java, javac y Maven (los tres
#      responden preguntas distintas: runtime invocado, compilador y Java
#      con el que corre el propio proceso de Maven). Cualquier discrepancia
#      queda registrada, no oculta.
#   2) Solo M11: levanta PostgreSQL con Docker Compose, recrea el esquema
#      desde cero (schema.sql) y comprueba que existan las tablas.
#   3) Ejecuta la construccion limpia: mvn clean test.
#   4) Si Maven informa BUILD SUCCESS, imprime MODULO_<MODULO>_VERIFICADO.
#      Si falla, imprime MODULO_<MODULO>_NO_VERIFICADO y conserva el error.
#
# Requiere: JDK y Maven en el PATH, con acceso al repositorio configurado
# (por defecto, Maven Central). Para M11 ademas: Docker en ejecucion y las
# variables de entorno DB_USER, DB_PASSWORD, DB_NAME y DB_PORT.

set -euo pipefail

MODULOS_VALIDOS=("M01" "M02" "M03" "M04" "M05" "M06" "M07" "M08" "M09" "M10" "M11")
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

SCHEMA_FILE="src/main/resources/sql/schema.sql"
SERVICIO_BD="postgres"

# Prepara la BD para M11. Cada comando lleva "|| return 1" porque, al llamarse
# desde un "||", set -e no aplica dentro de la funcion.
preparar_bd_m11() {
  for v in DB_USER DB_PASSWORD DB_NAME DB_PORT; do
    if [[ -z "${!v:-}" ]]; then
      echo "ERROR: falta la variable de entorno $v" >&2
      return 1
    fi
  done
  export DB_USER DB_PASSWORD DB_NAME DB_PORT

  [[ -f "$SCHEMA_FILE" ]] || { echo "ERROR: no existe $SCHEMA_FILE" >&2; return 1; }

  echo "-- docker --"
  docker --version || return 1
  docker compose version || return 1
  docker info >/dev/null 2>&1 || { echo "ERROR: el motor de Docker no responde (abre Docker Desktop)" >&2; return 1; }

  echo ""
  echo "[1/4] Reiniciando PostgreSQL desde cero (down -v + up)..."
  docker compose down -v || return 1
  docker compose up -d "$SERVICIO_BD" || return 1

  echo "[2/4] Esperando a que la BD acepte conexiones TCP..."
  local lista=false
  for _ in $(seq 1 30); do
    # -h 127.0.0.1 fuerza TCP: durante el arranque inicial solo hay socket
    # local, asi no confundimos el servidor temporal de init con el real.
    if docker compose exec -T "$SERVICIO_BD" pg_isready -h 127.0.0.1 -U "$DB_USER" -d "$DB_NAME" >/dev/null 2>&1; then
      lista=true
      break
    fi
    sleep 2
  done
  [[ "$lista" == true ]] || { echo "ERROR: la BD no respondio en 60 s" >&2; return 1; }

  echo "[3/4] Recreando esquema desde cero..."
  docker compose exec -T "$SERVICIO_BD" psql -U "$DB_USER" -d "$DB_NAME" \
    -v ON_ERROR_STOP=1 -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;" || return 1
  docker compose exec -T "$SERVICIO_BD" psql -U "$DB_USER" -d "$DB_NAME" \
    -v ON_ERROR_STOP=1 < "$SCHEMA_FILE" || return 1

  echo "[4/4] Comprobando tablas..."
  local t existe
  for t in ventas detalle_venta; do
    existe=$(docker compose exec -T "$SERVICIO_BD" psql -U "$DB_USER" -d "$DB_NAME" -tAc \
      "SELECT to_regclass('public.$t') IS NOT NULL" | tr -d '\r') || return 1
    [[ "$existe" == "t" ]] || { echo "ERROR: falta la tabla $t" >&2; return 1; }
    echo "   tabla $t: OK"
  done
}

fallo_modulo() {
  echo ""
  echo "== Resultado =="
  echo "MODULO_${MODULO}_NO_VERIFICADO"
  echo "La construccion fallo. Conserva este comando y su salida, identifica" >&2
  echo "la causa, corrige y vuelve a ejecutar. No declares MODULO_${MODULO}_VERIFICADO" >&2
  echo "sin haber ejecutado realmente la comprobacion." >&2
  exit 1
}

echo "== Registro de versiones efectivas =="
echo "-- java (runtime) --"
java -version
echo ""
echo "-- javac (compilador) --"
javac -version
echo ""
echo "-- mvn (Java con el que corre Maven) --"
mvn -version

if [[ "$MODULO" == "M11" ]]; then
  echo ""
  echo "== Preparando base de datos (M11) =="
  preparar_bd_m11 || fallo_modulo
fi

echo ""
echo "== Compilando y probando (mvn clean test) =="
if mvn -B clean test; then
  echo ""
  echo "== Resultado =="
  echo "MODULO_${MODULO}_VERIFICADO"
else
  fallo_modulo
fi