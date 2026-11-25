# Notas de versión — v1.0-rc (candidato de liberación)

Corte 4 (R05). Candidato a versión 1.0 del sistema de ventas: PostgreSQL 16, API REST (Spring Boot 2.7.18, Java 11) y cliente Angular 20 servido por nginx, en contenedores con Docker Compose. Etiqueta `v1.0-rc` sobre `master`.

## Versiones de los componentes
| Componente | Versión |
|---|---|
| Backend (`pom.xml`) | 1.0.0 |
| Cliente (`frontend/package.json`) | 1.0.0 |
| Contrato (`docs/openapi.yaml`) | 1.0.0 |
| Base de datos | PostgreSQL 16 |
| Cliente web en contenedor | nginx 1.27 (compilación con Node 24) |
| Ejecución de la API | JRE 11 (imagen eclipse-temurin 11-jre-alpine) |

Versiones efectivas y congeladas: `docs/versiones-congeladas.md` y `evidencia/C04/C04-01_versiones.txt`. La etiqueta `v1.0.0` es de una práctica anterior (C02) y no corresponde a este candidato.

## Qué incluye respecto a `v0.3-web`
- Pila en contenedores: `Dockerfile.api`, `Dockerfile.frontend`, `docker-compose.yml` con healthchecks y puertos configurables (`API_PORT` 9098, `WEB_PORT` 9095).
- Verificación reproducible: `verify-stack.sh` (con caída de la BD), `verify-recorrido.sh` y el módulo oficial `verify-module.sh M14`; `verify-persistencia.sh` para los dos adaptadores (`PERSISTENCIA=hibernate|jdbc`).
- Integración continua en GitHub Actions: backend con PostgreSQL, frontend y pila completa.
- Calidad: SonarQube en backend y frontend con los hallazgos corregidos (0 abiertos al cierre).
- Pruebas: 142 del backend (cobertura de líneas 87.7 %, ramas 71.4 %) y 34 del frontend; matriz de riesgos R1 a R16.
- Plan de reversa a `v0.3-web` con ensayo real (respaldo `pg_dump`, restauración y regreso).
- Correcciones: puerto por defecto, servidores del contrato (`openapi.yaml`), healthcheck del cliente (IPv4 en vez de `localhost`), cierre de diálogos modales en las pruebas de Swing bajo el CI. Sin cambios de esquema de base de datos respecto a `v0.3-web`.

## Artefactos
- `tcsw-ventas.jar` (API ejecutable), adjunto a la Release de GitHub con su suma SHA-256, y también como artefacto `tcsw-ventas-jar` del CI.
- Imágenes de contenedor: se construyen localmente con `docker compose up -d --build`; no se publican en un registro.

## Instalación, verificación y reversa
- Instalación y verificación: sección "Candidato de liberación v1.0-rc" del `README.md`.
- Reversa: `docs/plan-reversa.md`.

## Estado de verificación
| Elemento | Estado |
|---|---|
| Pila en contenedores y recorrido de venta (M14) | VERIFICADO (Noe y, en otro equipo, Xochitl) |
| Caída y recuperación de la base de datos | VERIFICADO |
| Persistencia en ambos modos (hibernate y jdbc) | VERIFICADO |
| CI en `master` | VERIFICADO |
| SonarQube (backend y frontend) | VERIFICADO, local |
| Ensayo de reversa a `v0.3-web` | VERIFICADO |
| Concurrencia: dos ventas simultáneas sobre la misma existencia (R13) | PENDIENTE: sin prueba diseñada |
| SonarQube dentro del CI | NO_VERIFICADO: la instancia es local |
| Pruebas del backend con JDK 23 en el equipo de Xochitl | NO_VERIFICADO (E-02) |
| Reproducción en Linux o macOS | NO_VERIFICADO |

## Problemas conocidos
- El nombre fijo del contenedor `postgres_ventas` choca con pilas anteriores del mismo nombre (E-01).
- Las pruebas de persistencia necesitan `DB_PASSWORD` en el entorno (E-02).
- El CI puede fallar de forma intermitente por un `429` de Maven Central; se resuelve reintentando (E-03).
- Detalle: `docs/hallazgos-reproduccion-c04.md`.
