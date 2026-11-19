#!/usr/bin/env bash
# C03-04: pruebas del backend y del cliente Angular y verificación oficial M13, con salidas sin rutas personales.
# Uso (desde cualquier carpeta del repo): bash scripts/evidencia-c03-pruebas.sh <etiqueta>     p. ej.: xochitl
# Requisitos: Docker con la base arriba (docker compose up -d) y Chrome. El .env se carga solo (no se imprime).
# No tener abierto "npm start" (npm ci puede fallar con EPERM).
cd "$(dirname "$0")/.." || exit 1
ETQ="${1:?Uso: $0 etiqueta (por ejemplo: xochitl)}"
DIR=evidencia/C03
mkdir -p "$DIR"
if [ -f .env ]; then set -a; . ./.env; set +a; fi

limpiar() { # quita rutas del equipo, colores y retornos de carro
  sed -E 's/\x1b\[[0-9;]*[A-Za-z]//g' | tr -d '\r' | sed -E 's#[A-Za-z]:[\\/][^ ]*#<ruta>#g; s#/(c|home|Users)/[^ ]*#<ruta>#g'
}
LOG=$(mktemp)

echo "== Versiones =="
{
  echo "java: $(java -version 2>&1 | head -1)"
  echo "maven: $(mvn -v 2>&1 | head -1)"
  echo "node: $(node -v)  npm: $(npm -v)"
  echo "angular cli: $(cd frontend && npx ng version 2>/dev/null | grep -m1 'Angular CLI')"
} | limpiar | tee "$DIR/C03-04_versiones_${ETQ}.txt"

echo; echo "== Backend: mvn clean verify =="
mvn -B clean verify >"$LOG" 2>&1; RC_MVN=$?
grep -E "Tests run:|BUILD (SUCCESS|FAILURE)|ERROR\]" "$LOG" | limpiar > "$DIR/C03-04_backend_${ETQ}.txt"
tail -6 "$DIR/C03-04_backend_${ETQ}.txt"

echo; echo "== Cliente Angular: ng test =="
( cd frontend && npm ci --no-audit --no-fund >"$LOG" 2>&1 && npx ng test --watch=false --browsers=ChromeHeadless --progress=false >>"$LOG" 2>&1 ); RC_NG=$?
grep -E "Executed [0-9]+ of [0-9]+|TOTAL|FAILED" "$LOG" | limpiar | tail -4 > "$DIR/C03-04_angular_${ETQ}.txt"
cat "$DIR/C03-04_angular_${ETQ}.txt"

echo; echo "== Verificación oficial M13 =="
./scripts/verify-module.sh M13 >"$LOG" 2>&1; RC_M13=$?
limpiar < "$LOG" > "$DIR/C03-04_verify-m13_${ETQ}.txt"
tail -3 "$DIR/C03-04_verify-m13_${ETQ}.txt"

rm -f "$LOG"
echo; echo "== Resumen =="
[ $RC_MVN -eq 0 ] && echo "VERIFICADO: backend (mvn clean verify)" || echo "NO_VERIFICADO: backend (código $RC_MVN)"
[ $RC_NG  -eq 0 ] && echo "VERIFICADO: pruebas de Angular" || echo "NO_VERIFICADO: pruebas de Angular (código $RC_NG)"
[ $RC_M13 -eq 0 ] && echo "VERIFICADO: verify-module.sh M13" || echo "NO_VERIFICADO: verify-module.sh M13 (código $RC_M13)"
echo "Salidas en $DIR/C03-04_*_${ETQ}.txt: revisa que no contengan rutas, usuarios ni contraseñas antes del commit."
