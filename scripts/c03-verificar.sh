#!/usr/bin/env bash
# Verifica el índice evidencia/C03_EQUIPO_06.md antes del commit de cierre:
#  1) que cada ruta citada exista, 2) que no queden marcadores sin completar, 3) que no haya datos personales en las evidencias.
cd "$(dirname "$0")/.." || exit 1
IDX=evidencia/C03_EQUIPO_06.md
FALLOS=0
[ -f "$IDX" ] || { echo "No existe $IDX" >&2; exit 1; }

echo "== Rutas citadas en el índice =="
for ruta in $(grep -o '`[^`]*`' "$IDX" | tr -d '`' | grep -E '^(evidencia|docs|scripts|frontend|src)/' | grep -v '\*' | sort -u); do
  if [ -e "$ruta" ]; then echo "ok        $ruta"; else echo "FALTA     $ruta"; FALLOS=$((FALLOS+1)); fi
done

echo; echo "== Marcadores sin completar =="
if grep -rn -E '<<|COMPLETAR|TODO' "$IDX" docs/guia-reproduccion-c03.md docs/registro-errores-c03.md 2>/dev/null; then FALLOS=$((FALLOS+1)); else echo "ninguno"; fi

echo; echo "== Datos personales en las salidas de evidencia =="
if grep -rn -i -E 'password=|users[\\/]|/home/|/c/users' evidencia/C03/*.txt evidencia/C03/*.md 2>/dev/null; then FALLOS=$((FALLOS+1)); else echo "ninguno"; fi

echo; echo "== Registro de errores =="
if grep -qE '^\| 1 \| *\|' docs/registro-errores-c03.md 2>/dev/null; then echo "La fila 1 sigue vacía: completa o borra la fila de ejemplo"; FALLOS=$((FALLOS+1)); else echo "ok"; fi

echo
if [ "$FALLOS" -eq 0 ]; then echo "VERIFICACION_C03_OK"; else echo "VERIFICACION_C03_CON_PENDIENTES ($FALLOS)"; exit 1; fi
