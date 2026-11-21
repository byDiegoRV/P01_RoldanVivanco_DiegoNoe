# Integración continua (C04)

Archivo: `.github/workflows/ci.yml` (GitHub Actions). Se ejecuta en cada `push` a `master` y a ramas `feature/**`, en cada *pull request* hacia `master` y a demanda (`workflow_dispatch`).

| Job | Qué hace | Qué demuestra |
|---|---|---|
| `backend` | Java 11 (Temurin), PostgreSQL 16 como servicio, crea el esquema desde `schema.sql`, `mvn -B verify` (con Xvfb para las pruebas Swing), publica reportes de Surefire y JaCoCo y el jar | Pruebas del backend contra una BD real y cobertura |
| `frontend` | Node 24, `npm ci`, `ng build`, `ng test` en Chrome sin interfaz | El cliente compila y sus pruebas pasan con dependencias fijadas por el lock |
| `pila` | (necesita los dos anteriores) `docker compose build`, levanta PostgreSQL + API + nginx, ejecuta `verify-stack.sh` y `verify-recorrido.sh` | Integración vertical cliente → API → PostgreSQL en un entorno limpio, sin la máquina de nadie del equipo |

## Qué queda fuera (declarado)
- **SonarQube** no corre en CI: la instancia es local (`localhost:9000`) y un *runner* de GitHub no puede alcanzarla. Se ejecuta manualmente y se registra en `evidencia/C04/` (issue C04-04). Estado: **PENDIENTE de automatizar** (alternativa fuera de alcance: SonarCloud).
- La contraseña de CI (`DB_PASSWORD`) es un valor de prueba no secreto, propio del contenedor efímero del *runner*; no se usan secretos reales.

## Cómo verificarlo
1. Pestaña **Actions** del repositorio → ejecución más reciente de **CI**: los tres jobs en verde.
2. En la ejecución, sección *Artifacts*: `backend-reportes` (Surefire + JaCoCo) y `tcsw-ventas-jar`.
3. Resumen de pruebas en el *Summary* del job `backend`.

## Registro de ejecuciones
## Registro de ejecuciones
| Ejecución | Commit | Resultado | Observación |
| :--- | :--- | :--- | :--- |
| #1 | `cf6c9cf` | Cancelada a los 29 min | Bloqueo en `VentasControllerTest` por diálogos modales (H-03) |
| #2 | `66616ae` | Fallida | `pila` falló: `Permission denied`, faltaba el bit de ejecución en Git |
| #3 | `ad11bcb` | Verde, 2 min 33 s | 142 pruebas, 0 fallos; 2 artefactos |
| #4 | `28408d7` | Verde, 2 min 26 s | Registro de ejecuciones y evidencia |
| #5 | `edb14b8` | Cancelada (2 min 4 s) | Probablemente interrumpida por un push posterior (no verificado) |
| #6 | `5a1b061` | Cancelada (1 min 20 s) | Probablemente interrumpida por un push posterior (no verificado) |
| #7 | `679fbda` | Verde, 2 min 36 s | Versiones efectivas con rutas ocultas |
| #8 | `e6498e3` | Verde, 2 min 15 s | Análisis final de Sonar |
| #9 | `45c6c94` | Verde, 2 min 20 s | Capturas de C04-05 |
| #10 | `e1a5d73` | Verde, 2 min 29 s | Verificación de persistencia por modo |
| #11 | `f062563` | Verde, 2 min 19 s | Ensayo de reversa ejecutado |
| #12 | `480a32e` | Verde, 2 min 57 s | Estado final de la rama antes del PR a `master` |

Enlace a la ejecución verde: https://github.com/byDiegoRV/P01_RoldanVivanco_DiegoNoe/actions/runs/44871294470
