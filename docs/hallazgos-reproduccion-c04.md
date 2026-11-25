# Hallazgos de la reproducción del candidato por otra persona (C04-07)

La reproducción la hizo Xochitl Salgado en su equipo (Windows, Docker Desktop, JDK 23) desde un clon limpio de `master` (commit `e744604`), PR #79. Su evidencia está en `evidencia/C04/C04-07_*` y su registro en `docs/registro-errores-c04.md` (solo contiene E-01). Este documento, redactado por Diego Noe Roldán Vivanco a partir de esa evidencia y del PR, recoge los tres hallazgos observados. Ninguno se corrigió en el candidato, para no invalidar lo ya reproducido; se documentan y se mitigan en el README.

Estados: VERIFICADO / NO_VERIFICADO / PENDIENTE.

| ID | Qué se observó (hecho) | Hipótesis | Prueba | Mitigación | Estado |
|---|---|---|---|---|---|
| E-01 | `docker compose up` falló con `Conflict. The container name "/postgres_ventas" is already in use`; M14 terminó en `MODULO_M14_NO_VERIFICADO` (`C04-07_m14_xochitl_intento1.txt`) | El compose fija `container_name: postgres_ventas` y el equipo ya tenía un contenedor con ese nombre de una práctica anterior | `docker ps -a --filter "name=postgres_ventas"` mostró un contenedor detenido de otro proyecto Compose | Apartarlo (`docker rename`) y repetir; advertencia en el README. No se cambió el compose | VERIFICADO (segundo intento: `MODULO_M14_VERIFICADO`, `C04-07_m14_xochitl.txt`) |
| E-02 | `mvn -B verify` local: 21 errores en 4 clases de persistencia con `IllegalStateException: Define la variable de entorno DB_PASSWORD antes de ejecutar las pruebas` (`C04-07_backend_xochitl.txt`) | Un `mvn` directo no exporta el `.env`; las pruebas exigen `DB_PASSWORD` (el fallo es de JDBC puro e Hibernate por igual, no del JDK) | Mensaje del reporte de surefire (`PostgresProductoAdapterTest.java:25`) | `set -a; source .env; set +a` antes de `mvn` (README). El CI define sus propias variables y pasa | NO_VERIFICADO en su equipo (el backend local no se repitió con la variable); VERIFICADO en CI y en el equipo de Noe |
| E-03 | En el CI de su rama, el job Backend del `push` falló a los 31 s: `Could not transfer artifact org.jacoco:jacoco-maven-plugin:pom:0.8.11 ... status code: 429, Too Many Requests`; el mismo commit pasó en el job del `pull_request` | Límite de peticiones de Maven Central al descargar dependencias en el runner; no es un fallo del código (el PR solo agregaba documentación y evidencia) | El log mostró el 429 antes de compilar | Re-ejecutar los jobs fallidos (pasó). Mejora no aplicada: caché de Maven en `actions/setup-java` y reintentos HTTP | VERIFICADO (los 6 checks del PR #79 en verde tras el reintento) |

## Límites declarados de la reproducción
- Un solo equipo (Windows). No se probó en Linux ni en macOS.
- No se incluyeron capturas de pantalla de la reproducción: la evidencia son las salidas de terminal de `evidencia/C04/C04-07_*.txt` (versiones, M14 en dos intentos, caída de la base de datos y backend).
- La prueba manual en el navegador no se ejecutó en su equipo; el recorrido por nginx lo cubre `verify-recorrido.sh` dentro de M14.
- La descripción del PR #79 contiene "run #16", que GitHub enlaza al PR #16 de otra práctica (se refería a la ejecución 16 del CI) y no incluye `Closes #76`; el issue #76 se cerró manualmente.
- Su sección de `docs/contribuciones/integrante-2.md` fue redactada por Noe a partir de la evidencia, y así se declara en ese archivo.
