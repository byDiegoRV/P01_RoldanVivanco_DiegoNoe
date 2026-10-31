# Contribución — Diego Noe Roldán Vivanco (GitHub: byDiegoRV)

**Tareas de C02:** C02-01 a C02-04 (issues #33 a #36), integración del PR #39 y del PR #40, cierre del corte.

## Qué hice
- **C02-01 Base reproducible:** configuración por variables de entorno (`.env.example`, `DB_PORT`), `docker-compose.yml` parametrizado, pruebas de base de datos con fixture compartido (`BaseDeDatosDePruebas`) y diagnóstico del conflicto del puerto 5434 (se movió a 5435).
- **C02-02 Venta transaccional JDBC:** tablas `ventas` y `detalle_venta`, `VentaJdbcRepository` con una sola transacción, rollback con causa conservada y descuento de existencias protegido; pruebas con estado final comprobado y dos sabotajes como evidencia negativa.
- **C02-03 Hibernate:** entidades, `hibernate.cfg.xml` y adaptadores de producto y venta sobre los mismos puertos; corrección de un fallo de rollback encontrado por las pruebas.
- **C02-04 Swing con persistencia:** `MainSwing`, `ConfiguracionBaseDeDatos`, controlador con `SwingWorker`, `scripts/datos-demo.sql` y 7 pruebas del controlador.
- **Calidad:** análisis con SonarQube, corrección de S1141, S1135 y S2925, y estabilización de una prueba intermitente.
- **Cierre:** regeneración de la evidencia de `verify-module.sh M11`, corrección del README y del script, bitácora de fallos, nota de aislamiento e índice de evidencias.

## Commits (autor registrado: «Estudiante TCSW-19234 <estudiante@example.com>», por identidad de Git no configurada en mi equipo)
11394dc, 8c9f86e, 6fbb747, 263945d, 2df5253, 176e14d, 8cb03d4, 5f85126, a0da974, 80936fc, 07f55c4, 91b2807, 7209bc4, 8e08471, 273a54f — todos en el PR #39. Los commits posteriores a la corrección de la identidad llevan mi nombre.

## Pull requests
Autor del #39. Integré el #40 de Xochitl. Revisión cruzada: <completado: PR revisado, fecha y resultado>.

## Dificultades
Puerto 5434 ocupado por otro servidor, rollback de Hibernate ejecutado después de cerrar la sesión, token de SonarQube expuesto en un archivo de evidencia y una prueba de Swing intermitente. Todas están detalladas en `evidencia/C02/bitacora-fallos.md`.

Asumí además C02-07 y C02-08 (índice, bitácora, aislamiento y etiqueta) porque no se entregaron en su asignación original