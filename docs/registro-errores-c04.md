# Registro de errores de la reproducción (C04-07)

Quién reprodujo: Xochitl Salgado. Fecha: 2026-11-24. Commit de `master` clonado: `e744604` (Merge pull request #78, `feature/c04-contenedores`).
Método: cada fila separa lo observado (hecho) de la explicación (hipótesis); la hipótesis se confirma o se descarta con una prueba.
Estados: VERIFICADO / NO_VERIFICADO / PENDIENTE.

| ID | Paso | Qué se observó (hecho, mensaje exacto) | Hipótesis | Prueba que la confirma o descarta | Corrección | Estado |
|---|---|---|---|---|---|---|
| E-01 | Verificación M14 (`./scripts/verify-module.sh M14`) | `docker compose up` falló con `Error response from daemon: Conflict. The container name "/postgres_ventas" is already in use by container "7ec1f34e..."`. El script terminó con `STACK_C04_NO_VERIFICADO (9 comprobaciones fallidas)` y `MODULO_M14_NO_VERIFICADO`. Evidencia: `evidencia/C04/C04-07_m14_xochitl_intento1.txt` | El `docker-compose.yml` fija `container_name: postgres_ventas` y mi equipo ya tenía un contenedor con ese nombre de una práctica anterior; las otras 8 comprobaciones fallaron en cascada porque la pila no arrancó | `docker ps -a --filter "name=postgres_ventas"` mostró el contenedor `7ec1f34ea97a`, detenido (`Exited (255)`), con el puerto 5435 y perteneciente al proyecto Compose `p01_roldanvivanco_diegonoe` (mi clon anterior), no al clon nuevo | Se renombró el contenedor viejo sin borrarlo ni su volumen (`docker rename postgres_ventas postgres_ventas_c03_anterior`), se ejecutó `docker compose down` para limpiar el intento fallido y se repitió M14 | VERIFICADO: segundo intento con `STACK_VERIFICADO`, `RECORRIDO_VERIFICADO` y `MODULO_M14_VERIFICADO` (`evidencia/C04/C04-07_m14_xochitl.txt`) |

## Resumen
- Errores encontrados: 1 (E-01), de entorno, no de código. Se resolvió a la primera con la causa identificada.
- Hallazgo para la documentación: `postgres_ventas` es un nombre de contenedor fijo.
- Aparte: también hice el clon inicialmente dentro de otro clon y sobre la rama `main` (que solo tiene el commit inicial); el candidato está en `master`. Se corrigió clonando en una carpeta aparte y creando la rama desde `origin/master`.
- Versiones de mi equipo: ver `evidencia/C04/C04-07_versiones_xochitl.txt`.