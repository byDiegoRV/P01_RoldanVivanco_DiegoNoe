# Versiones congeladas — candidato de liberación v1.0-rc

Congelar significa que **no se actualiza ninguna versión durante la integración** (M14): lo que se prueba es exactamente lo que se entrega. Cualquier cambio de versión se registra aquí con motivo y fecha.

| Componente | Versión congelada | Dónde queda fijada |
|---|---|---|
| Java (compilación y ejecución de la API) | 11 (Temurin) | `Dockerfile.api` (`maven:3.9-eclipse-temurin-11`, `eclipse-temurin:11-jre-alpine`) y `pom.xml` |
| Maven | 3.9 | imagen `maven:3.9-eclipse-temurin-11` |
| Spring Boot | 2.7.18 | `pom.xml` (`spring-boot.version`) |
| PostgreSQL | 16 | `docker-compose.yml` (`postgres:16`) |
| Node.js (compilación del cliente) | 24 | `Dockerfile.frontend` (`node:24-alpine`) |
| Angular | 20.x, versión exacta resuelta en `frontend/package-lock.json` | `npm ci` instala exactamente lo del lock |
| nginx (servidor del cliente) | 1.27 | `Dockerfile.frontend` (`nginx:1.27-alpine`) |
| Docker Compose | v2 (`docker compose`) | requisito del entorno |
| Versión del producto | (completar: la de `pom.xml`, `frontend/package.json` y `docs/openapi.yaml`) | debe coincidir con la etiqueta `v1.0-rc` |

Versiones efectivas registradas con ejecución real: `evidencia/C04/C04-01_versiones.txt` (salida de `scripts/evidencia-c04-versiones.sh`).

## Reglas
- No se ejecuta `npm update`, `npm install <paquete>@latest` ni se cambia `spring-boot.version`.
- Se instala siempre con `npm ci` (respeta el lock) y `mvn` con el `pom.xml` del commit.
- Entornos probados: (completar con el equipo, p. ej. Noe: JDK 11 / Node 24.11 / Windows 11; Xochitl: JDK 23 / Node 24.21 / Windows) — la API se compila con JDK 11 dentro de la imagen, por eso el contenedor no depende del JDK local.
