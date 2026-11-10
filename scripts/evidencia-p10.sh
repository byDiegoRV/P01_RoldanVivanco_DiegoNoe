#!/usr/bin/env bash
# Genera la evidencia de build y pruebas del cliente Angular (P10).
# Uso (desde la raiz del repositorio):  bash scripts/evidencia-p10.sh P10-04
# Guarda salidas resumidas en evidencia/P10/. No incluye rutas personales ni secretos.
set -u

ETIQUETA="${1:-P10}"
RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
DESTINO="$RAIZ/evidencia/P10"

if [ ! -d "$RAIZ/frontend" ]; then
  echo "No se encontro la carpeta frontend en $RAIZ" >&2
  exit 1
fi

mkdir -p "$DESTINO"
cd "$RAIZ/frontend" || exit 1

limpiar() {
  # Quita colores ANSI y retornos de carro.
  sed -E 's/\x1b\[[0-9;?]*[A-Za-z]//g' | tr '\r' '\n'
}

{
  echo "== $ETIQUETA: build =="
  echo "Fecha: $(date '+%Y-%m-%d %H:%M')"
  echo "Node: $(node -v)  npm: $(npm -v)"
  npx ng version 2>/dev/null | limpiar | grep -E 'Angular CLI|Angular:|^Node|Package Manager' || true
  echo "-- ng build --"
  npx ng build 2>&1 | limpiar \
    | sed -E 's#Output location:.*#Output location: dist/frontend#' \
    | grep -v '^\s*$'
  echo "Codigo de salida del build: ${PIPESTATUS[0]}"
} > "$DESTINO/${ETIQUETA}_build.txt" 2>&1

{
  echo "== $ETIQUETA: pruebas =="
  echo "Fecha: $(date '+%Y-%m-%d %H:%M')"
  npx ng test --watch=false --browsers=ChromeHeadless --progress=false 2>&1 | limpiar > "$DESTINO/.test_crudo.tmp"
  grep -E 'TOTAL|FAILED|ERROR' "$DESTINO/.test_crudo.tmp" | sort -u
  grep 'Executed' "$DESTINO/.test_crudo.tmp" | tail -n 1
} > "$DESTINO/${ETIQUETA}_test.txt" 2>&1

rm -f "$DESTINO/.test_crudo.tmp"

echo
echo "Evidencia generada:"
echo "  evidencia/P10/${ETIQUETA}_build.txt"
echo "  evidencia/P10/${ETIQUETA}_test.txt"
echo
echo "--- resumen de pruebas ---"
cat "$DESTINO/${ETIQUETA}_test.txt"
