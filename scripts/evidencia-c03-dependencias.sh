#!/usr/bin/env bash
# Genera la evidencia de dependencias de las dos interfaces (REST y Swing) hacia el núcleo.
# Uso (desde cualquier carpeta del repo): bash scripts/evidencia-c03-dependencias.sh antes|despues
set -euo pipefail
cd "$(dirname "$0")/.."
ETQ="${1:?Uso: $0 antes|despues}"
BASE=src/main/java/mx/uv/tcsw/ventas
SAL="evidencia/C03/C03-01_dependencias_${ETQ}.txt"
mkdir -p evidencia/C03

imports() { # capa, paquete(s) regex
  grep -rhoE "^import mx\.uv\.tcsw\.ventas\.($2)\.[A-Za-z0-9_.]+" "$BASE/infrastructure/delivery/$1" | sed 's/^import //' | sort -u || true
}

{
  echo "# C03-01 Dependencias de las interfaces hacia el núcleo (${ETQ} del refactor)"
  for capa in rest swing; do
    echo
    echo "## delivery/${capa}: imports de application y domain"
    imports "$capa" "application|domain"
  done
  echo
  echo "## Puertos de entrada (application.ports.in) usados por AMBAS interfaces"
  comm -12 <(imports rest "application\.ports\.in") <(imports swing "application\.ports\.in")
  echo
  echo "## Puertos de salida (application.ports.out) usados directamente por cada interfaz"
  for capa in rest swing; do
    echo "- ${capa}:"
    imports "$capa" "application\.ports\.out" | sed 's/^/    /'
  done
  echo
  echo "## Dónde se arma una venta en el código de producción (agregarPartida y formato de folio)"
  grep -rn "agregarPartida\|FORMATO_FOLIO" "$BASE" | cut -c1-150 || true
} > "$SAL"

echo "Generado: $SAL"
cat "$SAL"
