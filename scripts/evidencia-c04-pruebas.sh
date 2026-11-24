#!/usr/bin/env bash
# evidencia-c04-pruebas.sh - reporte de pruebas y cobertura del candidato (C04-05)
# Uso (Git Bash, PostgreSQL del compose arriba):
#   ./scripts/evidencia-c04-pruebas.sh 2>&1 | tee evidencia/C04/C04-05_reporte_pruebas.txt
# Ejecuta mvn verify (142 pruebas + JaCoCo) y ng test con cobertura, y resume ambos.
set -uo pipefail
cd "$(dirname "$0")/.."
if [[ -f .env ]]; then set -a; source .env; set +a; fi
docker compose up -d postgres >/dev/null 2>&1

echo "== C04-05 Reporte de pruebas y cobertura  $(date '+%Y-%m-%d %H:%M:%S') =="
echo "commit: $(git rev-parse --short HEAD)  rama: $(git branch --show-current)"
git diff --quiet HEAD -- src frontend/src pom.xml || echo "AVISO: hay cambios sin commit; el reporte no corresponde a un commit exacto"

echo ""; echo "== [1] Backend: mvn verify =="
mvn -B verify 2>&1 | grep -E "Tests run:|BUILD (SUCCESS|FAILURE)|ERROR\]" | sed 's/Time elapsed: [0-9.,]* s - //' | tail -45
CSV=target/site/jacoco/jacoco.csv
if [[ -f "$CSV" ]]; then
  echo ""; echo "== [2] Cobertura del backend por paquete (JaCoCo) =="
  awk -F, 'NR>1 { p=$2; li[p]+=$8; lc[p]+=$9; bi[p]+=$6; bc[p]+=$7 }
    END { for (p in li) { t=li[p]+lc[p]; b=bi[p]+bc[p];
      printf "%-62s lineas %5.1f%% (%d/%d)  ramas %s\n", p, (t?100*lc[p]/t:0), lc[p], t, (b? sprintf("%.1f%% (%d/%d)",100*bc[p]/b,bc[p],b):"n/a") } }' "$CSV" | sort
  awk -F, 'NR>1 {li+=$8; lc+=$9; bi+=$6; bc+=$7} END {printf "TOTAL lineas %.1f%% (%d/%d)  ramas %.1f%% (%d/%d)\n",100*lc/(li+lc),lc,li+lc,100*bc/(bi+bc),bc,bi+bc}' "$CSV"
  echo ""; echo "== [3] Clases con menos de 70 % de lineas cubiertas (donde hay riesgo sin medir por pruebas unitarias) =="
  awk -F, 'NR>1 { t=$8+$9; if (t>0 && 100*$9/t<70) printf "%-78s %5.1f%% (%d/%d)\n", $2"."$3, 100*$9/t, $9, t }' "$CSV" | sort
else
  echo "NO_VERIFICADO: no se genero $CSV"
fi

echo ""; echo "== [4] Frontend: ng test con cobertura =="
( cd frontend && npx ng test --watch=false --browsers=ChromeHeadless --progress=false --code-coverage 2>&1 \
  | sed -E 's/\x1b\[[0-9;?]*[A-Za-z]//g' | tr '\r' '\n' | grep -E "TOTAL|Executed [0-9]+ of [0-9]+ .*(SUCCESS|FAILED)|Statements|Branches|Functions|Lines +:" | tail -8 )
echo ""; echo "== Fin =="
