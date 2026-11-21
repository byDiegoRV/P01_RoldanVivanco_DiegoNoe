#!/usr/bin/env bash
# C03: registra el resultado del análisis de SonarQube (ya ejecutado) en evidencia/C03/C03_sonar_final.md.
# Lee la API de SonarQube con el token de la variable de entorno SONAR_TOKEN; el token NO se imprime ni se guarda.
# Uso: bash scripts/evidencia-c03-sonar.sh        (después de correr el análisis con mvn ... sonar:sonar)
cd "$(dirname "$0")/.." || exit 1
HOST="${SONAR_HOST:-http://localhost:9000}"
KEY="${SONAR_KEY:-mx.uv.tcsw:tcsw-ventas}"
[ -n "${SONAR_TOKEN:-}" ] || { echo "Falta SONAR_TOKEN: read -rs SONAR_TOKEN && export SONAR_TOKEN" >&2; exit 1; }
SAL=evidencia/C03/C03_sonar_final.md
mkdir -p evidencia/C03

api() { curl -s -u "$SONAR_TOKEN:" "$HOST$1"; }
GATE=$(api "/api/qualitygates/project_status?projectKey=$KEY")
MED=$(api "/api/measures/component?component=$KEY&metricKeys=bugs,vulnerabilities,code_smells,security_hotspots,coverage,duplicated_lines_density,ncloc")
AN=$(api "/api/project_analyses/search?project=$KEY&ps=1")

GATE="$GATE" MED="$MED" AN="$AN" SAL="$SAL" node -e '
const g = JSON.parse(process.env.GATE), m = JSON.parse(process.env.MED), a = JSON.parse(process.env.AN);
if (!g.projectStatus || !m.component) { console.error("SonarQube no respondió con datos: revisa que el análisis se haya ejecutado y que el token sea válido."); process.exit(1); }
const v = {}; for (const x of m.component.measures) v[x.metric] = x.value;
const an = (a.analyses || [])[0] || {};
const gate = g.projectStatus.status === "OK" ? "Passed" : g.projectStatus.status;
const f = (k, s = "") => (v[k] !== undefined ? v[k] + s : "sin dato");
const txt = [
  "# Análisis de SonarQube final — C03", "",
  `**Fecha del análisis:** ${an.date || "sin dato"} | **Versión del proyecto:** ${an.projectVersion || "sin dato"} | **Alcance:** backend (Java), código del repositorio integrado`, "",
  "| Métrica (código completo) | Resultado |", "|---|---|",
  `| Quality Gate | ${gate} |`,
  `| Bugs | ${f("bugs")} |`, `| Vulnerabilidades | ${f("vulnerabilities")} |`,
  `| Code smells | ${f("code_smells")} |`, `| Security hotspots | ${f("security_hotspots")} |`,
  `| Cobertura | ${f("coverage", "%")} |`, `| Duplicaciones | ${f("duplicated_lines_density", "%")} |`,
  `| Líneas de código | ${f("ncloc")} |`, "",
  "Capturas: `capturas/C03_sonar_final_resumen.png` y `capturas/C03_sonar_final_issues.png`.", "",
  "## Estado", "**VERIFICADO:** resultado anterior para el código Java.",
  "**NO_VERIFICADO:** análisis del código TypeScript del cliente (Sonar cubre solo el backend).", ""
].join("\n");
require("fs").writeFileSync(process.env.SAL, txt);
console.log(txt);
'
