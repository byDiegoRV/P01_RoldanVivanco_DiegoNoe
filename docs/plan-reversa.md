# Plan de reversa — candidato v1.0-rc → v0.3-web

Estado del ensayo: **VERIFICADO** (intento 2, ver sección 6).

## 1. Alcance
- Volver el sistema (cliente Angular + API + BD en contenedores) de `v1.0-rc` a la versión estable anterior `v0.3-web` (commit `02ee70b`, cierre de C03) y poder regresar al candidato.
- Entre `v0.3-web` y el candidato el código de la aplicación casi no cambia (5 archivos de frontend: 13 líneas de estilos y 3 de plantillas) y el esquema de BD (`src/main/resources/sql/schema.sql`) no cambió; lo que cambia es la **definición de despliegue** (Dockerfile.api/Dockerfile.frontend, Compose con tres servicios, healthchecks, CI y scripts de verificación).
- Fuera de alcance: migraciones de esquema (no hubo ninguna; si el candidato las hubiera tenido, la reversa de datos requeriría un script inverso, no solo restaurar el respaldo).

## 2. Cuándo activar la reversa
Cualquiera de estas condiciones tras desplegar `v1.0-rc`:
- `scripts/verify-stack.sh` o `scripts/verify-recorrido.sh` terminan con NO_VERIFICADO.
- Una venta falla de forma sostenida (API 5xx, o el cliente no puede registrar la venta).
- Se detecta pérdida o corrupción de datos.

## 3. Prerrequisitos
- Docker Desktop activo y `.env` válido (DB_USER, DB_PASSWORD, DB_NAME, DB_PORT; sin credenciales en el repositorio).
- Tag `v0.3-web` disponible localmente (`git fetch --tags`).
- Respaldo lógico reciente de la BD (paso 1 del procedimiento).

## 4. Procedimiento
1. **Respaldar** la BD antes de tocar nada:
   `docker compose exec -T postgres pg_dump -U "$DB_USER" -d "$DB_NAME" --clean --if-exists > backups/respaldo_<fecha>.sql`
   (`backups/` está ignorado por Git: los datos no se versionan).
2. **Bajar** la pila del candidato sin borrar volúmenes: `docker compose down` (nunca `-v` en este paso).
3. **Obtener la versión anterior**: `git worktree add --detach ../tcsw-ventas-rollback v0.3-web`; copiar a esa carpeta el `.env` y la definición de despliegue del candidato (Compose, Dockerfiles, nginx.conf, .dockerignore, scripts de verificación).
4. **Levantar** esa versión con un proyecto Compose propio (`COMPOSE_PROJECT_NAME=tcsw-rollback`): primero solo `postgres`, luego restaurar el respaldo (`psql ... < respaldo.sql`) y después `docker compose up -d --build`.
5. **Verificar**: `./scripts/verify-stack.sh`; comprobar que el frontend servido es el de `v0.3-web`; comparar filas y huella md5 de `productos`, `ventas` y `detalle_venta` contra el estado previo.
6. **Regresar** al candidato (cuando se corrija la causa): `docker compose -p tcsw-rollback down -v`, quitar el worktree, `docker compose up -d --build` en la carpeta del candidato; verificar con `verify-stack.sh` y `verify-recorrido.sh`.

El ensayo completo y automatizado de estos pasos es `scripts/ensayo-reversa.sh`.

## 5. Criterio de éxito de la reversa
- `STACK_C04_VERIFICADO` en la versión anterior.
- Frontend servido distinto al del candidato (prueba de que cambió la versión).
- Filas y md5 de las tres tablas idénticos a los previos al respaldo.
- Tras regresar: mismo frontend que antes, mismos datos y `RECORRIDO_VERIFICADO`.

## 6. Resultado del ensayo
| Concepto | Resultado |
|---|---|
| Fecha y commit del candidato | 2026-11-21 13:27:16, commit 1d4b29b |
| Respaldo generado | 7704 bytes (pg_dump, fuera del repositorio) |
| Datos restaurados = datos previos | Sí: filas y md5 idénticos en productos, ventas y detalle_venta |
| Verificación de pila en v0.3-web | STACK_C04_VERIFICADO en v0.3-web |
| Frontend distinto al del candidato | Candidato styles-GYMJ7Z7U.css vs v0.3-web styles-LUTW3MCZ.css |
| Regreso al candidato + recorrido | Sí: mismos datos y RECORRIDO_VERIFICADO |
| Tiempo total del ensayo / de la reversa | 105 s (ensayo completo, incluye reconstrucción de imágenes) |
| Estado | VERIFICADO |

## 7. Límites declarados
- El respaldo es lógico (pg_dump) y puntual: las ventas hechas después del respaldo y antes de la reversa se pierden si no se respalda justo antes.
- La reversa usa el **código** de `v0.3-web` con la **definición de despliegue** del candidato; el `docker-compose.yml`/`Dockerfile` originales del tag son anteriores y no incluyen el cliente en contenedor.
- El ensayo se hizo en un entorno local (Docker Desktop, una máquina); no se probó en otro entorno.
- Existen tags de prácticas anteriores (`v0.3.0`, `v0.4.0`, `v1.0.0`) que no forman parte de la línea de este candidato; `v1.0.0` corresponde a C02 y no debe confundirse con `v1.0-rc`.
- Hallazgo H-05 (ensayo de reversa, intento 1). La comprobación de "frontend distinto" falló porque la imagen web del candidato llevaba 5 horas sin reconstruirse: docker compose restart reinicia el contenedor pero no regenera la imagen, por lo que servía un frontend anterior a las correcciones de Sonar, idéntico al de v0.3-web. Los datos y el recorrido sí pasaron. Corrección: el ensayo ejecuta docker compose up -d --build antes de medir la línea de base y la guía de despliegue indica reconstruir siempre. Retest: intento 2 ROLLBACK_VERIFICADO..
