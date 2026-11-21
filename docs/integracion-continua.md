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
| Ejecución | Commit | Resultado | Observación |
| :--- | :--- | :--- | :--- |
| #1 | (commit del primer push) | Cancelada a los 26 min | Bloqueo en `VentasControllerTest` por diálogos modales (H-03) |
| #2 | (commit de la corrección de diálogos) | `pila` falló | `Permission denied`: faltaba el bit de ejecución en Git |
| #3 | `ad11bcb` | Verde, 2 min 33 s | 142 pruebas, 0 fallos; 2 artefactos |
Enlace a la ejecución verde: https://github.com/byDiegoRV/P01_RoldanVivanco_DiegoNoe/actions/runs/44871294470
