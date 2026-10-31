# Bitácora de fallos y hallazgos — C02

Registro de lo que falló, cómo se detectó, la causa y la corrección. Fechas en 2026.

| # | Fecha | Hallazgo | Causa | Corrección | Evidencia |
|---|---|---|---|---|---|
| 1 | 10-26 | Las pruebas fallaban con «no existe la relación detalle_venta» aunque el contenedor sí tenía las tablas | Dos servidores PostgreSQL escuchaban en el puerto 5434 (uno local y el contenedor); las pruebas llegaban al servidor equivocado | El contenedor pasó al puerto 5435 (`DB_PORT`), la URL por defecto de pruebas y `.env.example` usan 5435, y existe `DB_URL` para sobrescribirla | commit 263945d |
| 2 | 10-26 | `TRUNCATE` fallaba por clave foránea en `PostgresProductoAdapterTest` | Se truncaba solo `productos`, referenciada por `detalle_venta` | Un solo `TRUNCATE TABLE detalle_venta, ventas, productos RESTART IDENTITY` en `BaseDeDatosDePruebas` | commit 8cb03d4 |
| 3 | 10-26 | Sabotaje 1: comentar `revertir(...)` dejó las pruebas en verde | Al cerrar la conexión sin `commit`, PostgreSQL descarta la transacción; el rollback explícito no cambia el estado final observable | Se documentó como límite de la prueba. El `revertir` explícito se conserva para no depender del cierre de la conexión y para conservar la causa (`addSuppressed`) | `evidencia/C02/sabotaje-sin-transaccion.txt` |
| 4 | 10-26 | Sabotaje 2: comentar `setAutoCommit(false)` | Cada sentencia se confirmaba sola | Las pruebas fallaron como se esperaba (2 fallos y 1 error), lo que demuestra que sí detectan la ausencia de transacción. Se restauró el código | `evidencia/C02/sabotaje-sin-transaccion.txt` |
| 5 | 10-26 | Hibernate: tras un fallo intermedio quedaba el encabezado de la venta (`expected: <0> but was: <1>`) | `try-with-resources` cerraba la `Session` antes del `catch`; al liberar la conexión el pool restablece auto-commit y confirma la transacción abierta | `try/catch/finally`: el rollback se hace antes de cerrar la sesión | `evidencia/C02/hibernate-fallo-rollback.txt`, commit a0da974 |
| 6 | 10-26 | Token de SonarQube expuesto en un archivo de evidencia | Se pegó el token en un comando documentado | Se retiró del archivo. Permanece en el historial anterior a ese commit, por lo que el token debe estar revocado en SonarQube (confirmar antes de entregar) | commit 11394dc |
| 7 | 10-27 | Sonar S1141 (bloques `try` anidados) y S1135 (falso positivo por la palabra «todo» en un comentario) en `VentaJdbcRepository` | `guardar` anidaba dos `try`; el comentario contenía «todo se confirma» | `guardar` y `ejecutarEnTransaccion` separados, comentario reformulado | commit 91b2807 |
| 8 | 10-27 | `VentasControllerPersistenteTest` intermitente (falló 1 de 5 corridas) | Condición de carrera: el cierre de diálogos y las lecturas de la tabla se hacían desde un hilo distinto al de eventos | `javax.swing.Timer` para cerrar diálogos, lecturas desde el hilo de eventos y esperas con tiempo límite. 15 de 15 corridas estables | commit 8e08471 |
| 9 | 10-27 | Sonar S2925 (`Thread.sleep`) y S1135 en esa prueba | Espera con `Thread.sleep` y comentario con la palabra «todo» | `LockSupport.parkNanos` con límite de 5 s y comentario reformulado | commit 8e08471 |
| 10 | 10-30 | Los archivos `C02-05_positiva_2da.txt` y `C02-05_negativa_variable.txt` llegaron vacíos a `master`; `C02-05_negativa_modulo.txt` solo traía el comando | Se subieron antes de generar la salida | Se regeneraron ejecutando `verify-module.sh` | `docs/evidencia/C02-05_*.txt` |
| 11 | 10-30 | `verify-module.sh` aceptaba `M010` en lugar de `M10` | Error tipográfico en la lista de módulos válidos | Corregido a `M10` | commit de cierre |
| 12 | 10-30 | README con bloques de código sin cerrar, puerto 5434 y marcas `[cite: 1]` | Edición manual de varias secciones | README reestructurado y corregido | commit de cierre |

## Observaciones de trazabilidad
- Los commits de `Noe` en este corte aparecen con el autor «Estudiante TCSW-19234 <estudiante@example.com>» porque la identidad de Git no estaba configurada en su equipo. A partir del commit de cierre se firman con su nombre real. La lista de hashes de esos commits está en `docs/contribuciones/NOE_C02.md`.
- Los commits del Corte 2 usan `refs #1` a `refs #4`, pero los issues reales son **#33 a #36** (C02-01 a C02-04). Equivalencia: #1→#33, #2→#34, #3→#35, #4→#36. Los commits de Xochitl usan `refs #5` y `refs #6`.
- El commit `e5645a1` («actualiza documentacion en README y agrega evidencias») solo agregó un archivo de prueba, `VentaFrameTest.java`.
