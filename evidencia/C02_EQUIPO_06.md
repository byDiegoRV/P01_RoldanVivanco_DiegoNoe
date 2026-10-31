# Índice de evidencias — C02 (Corte 2: Escritorio y persistencia) — Equipo 06

Rúbrica R03. Integrantes: Diego Noe Roldán Vivanco y Xochitl Salgado con aportaciones registradas; Miranda R. Cruz no registró aportaciones en C02. El índice, la bitácora, la nota de aislamiento y el cierre (C02-07 y parte de C02-08) los elaboró Noe.
Tag de entrega: `v0.2-escritorio`. Pull requests: #39 (C02-01 a C02-04), #40 (C02-05 y C02-06).
Estados: VERIFICADO = se ejecutó y la evidencia está en el repositorio; NO_VERIFICADO = no se ejecutó; PENDIENTE = falta hacerlo.

## 1. Cliente de escritorio Swing/MVC conectado a PostgreSQL
| Evidencia | Ubicación | Estado |
|---|---|---|
| Arranque y selección de adaptador por entorno | `src/main/java/mx/uv/tcsw/ventas/infrastructure/delivery/swing/MainSwing.java`, `.../persistence/ConfiguracionBaseDeDatos.java` | VERIFICADO |
| Controlador con guardado en segundo plano (`SwingWorker`) y mensajes sin SQL | `.../delivery/swing/controlador/VentasController.java` | VERIFICADO |
| Pruebas del controlador en modo persistente (7) | `src/test/java/.../swing/controlador/VentasControllerPersistenteTest.java` | VERIFICADO |
| Datos de demostración reproducibles | `scripts/datos-demo.sql` | VERIFICADO |
| Recorrido manual: compra, existencias en BD, reinicio, error por falta de existencia | `evidencia/C02/capturas/` | PENDIENTE (completar con las capturas o eliminar la fila) |

## 2. Persistencia: JDBC (línea base) y Hibernate sobre los mismos puertos
| Evidencia | Ubicación | Estado |
|---|---|---|
| Adaptadores JDBC | `.../persistence/jdbc/PostgresProductoAdapter.java`, `VentaJdbcRepository.java` | VERIFICADO |
| Adaptadores Hibernate y entidades | `.../persistence/hibernate/`, `src/main/resources/hibernate.cfg.xml` | VERIFICADO |
| Esquema `productos`, `ventas`, `detalle_venta` con claves foráneas | `src/main/resources/sql/schema.sql` | VERIFICADO |
| Pruebas JDBC y Hibernate | `PostgresProductoAdapterTest`, `VentaJdbcRepositoryTest`, `HibernateProductoAdapterTest`, `HibernateVentaAdapterTest` | VERIFICADO |

## 3. Consistencia transaccional
| Evidencia | Ubicación | Estado |
|---|---|---|
| Una conexión, `commit` solo al final, rollback conservando la causa | `VentaJdbcRepository.guardar` y `ejecutarEnTransaccion` | VERIFICADO |
| Descuento con `UPDATE ... WHERE stock >= ?` (JDBC y HQL equivalente) | `VentaJdbcRepository`, `HibernateVentaAdapter` | VERIFICADO |
| Casos: venta válida, existencia insuficiente, falla intermedia; estado final comprobado por JDBC independiente | pruebas citadas en la sección 2 | VERIFICADO |
| Evidencia negativa: sabotaje sin transacción | `evidencia/C02/sabotaje-sin-transaccion.txt` | VERIFICADO |
| Evidencia negativa: fallo de rollback en Hibernate | `evidencia/C02/hibernate-fallo-rollback.txt` | VERIFICADO |

## 4. Pruebas y calidad
| Evidencia | Ubicación | Estado |
|---|---|---|
| `mvn clean test`: 112 pruebas, 0 fallos | `docs/evidencia/C02-05_mvn_test_ok.txt` | VERIFICADO |
| `verify-module.sh M11` termina en `MODULO_M11_VERIFICADO` | `docs/evidencia/C02-05_positiva_2da.txt` | VERIFICADO |
| Negativas: variable faltante y módulo inválido | `docs/evidencia/C02-05_negativa_variable.txt`, `C02-05_negativa_modulo.txt` | VERIFICADO |
| SonarQube: Quality Gate Passed, 0 issues (antes: 2 code smells) | `evidencia/C02/capturas/sonar-*.png` | PENDIENTE (agregar capturas) |
| SonarQube posterior al PR #40 | — | NO_VERIFICADO |

## 5. Reproducibilidad
| Evidencia | Ubicación | Estado |
|---|---|---|
| Guía de reproducción C02, estados y comprobaciones negativas | `README.md` (sección Corte 2) | VERIFICADO |
| `.env.example`, `docker-compose.yml` con variables, puerto 5435 | raíz del repositorio | VERIFICADO |
| Reconstrucción desde un clon limpio por otra persona | — | NO_VERIFICADO |

## 6. Colaboración y trazabilidad
| Evidencia | Ubicación | Estado |
|---|---|---|
| Issues C02-01 a C02-04 (#33 a #36) y C02-05, C02-06 | repositorio de GitHub | VERIFICADO |
| Rama única por responsable, un commit por tarea con `refs #n` | historial de `master` (PR #39 y #40) | VERIFICADO |
| Equivalencia `refs #1..#4` = issues #33..#36 | `evidencia/C02/bitacora-fallos.md` | VERIFICADO |
| Revisión cruzada de pull requests | comentarios y aprobaciones en #39 y #40 | PENDIENTE (confirmar y anexar capturas) |
| Contribuciones individuales | `docs/contribuciones/NOE_C02.md`, `XOCHILT_C02.md` | VERIFICADO |
| Bitácora de fallos | `evidencia/C02/bitacora-fallos.md` | VERIFICADO |
| Nota de aislamiento | `evidencia/C02/nota-aislamiento.md` | VERIFICADO |
| Etiqueta `v0.2-escritorio` | tag sobre `master` | PENDIENTE (crear tras el último merge) |
