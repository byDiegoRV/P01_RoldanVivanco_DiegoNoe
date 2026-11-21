#!/usr/bin/env bash
# evidencia-c04-versiones.sh - registra las versiones efectivas de la pila (congelado de versiones)
# Uso: ./scripts/evidencia-c04-versiones.sh 2>&1 | tee evidencia/C04/C04-01_versiones.txt
cd "$(dirname "$0")/.."
echo "== Versiones efectivas  $(date '+%Y-%m-%d %H:%M:%S') =="
echo "-- commit --";            git rev-parse --short HEAD; git branch --show-current
echo "-- java --";              java -version 2>&1
echo "-- mvn --";               mvn -version 2>&1 | head -3
echo "-- node / npm --";        node --version; npm --version
echo "-- docker --";            docker --version; docker compose version
echo "-- pom.xml: version del proyecto y Spring Boot --"
grep -m1 -n "<version>" pom.xml
grep -n "spring-boot.version\|<java.version>\|maven.compiler" pom.xml | head -5
echo "-- frontend: version resuelta (package-lock) --"
( cd frontend && npm ls @angular/core @angular/cli typescript --depth=0 2>&1 | head -8 )
echo "-- imagenes base declaradas --"
grep -h "^FROM" Dockerfile.api Dockerfile.frontend
grep -n "image:" docker-compose.yml
echo "-- imagenes locales (si ya se construyo) --"
docker images --format '{{.Repository}}:{{.Tag}}  {{.ID}}' 2>/dev/null | grep -E "tcsw|postgres|nginx|temurin|node|maven" | head -10
