# Nota de aislamiento de pruebas — C02

**Qué se aísla**
- Las pruebas de persistencia usan una base dedicada (`ventas_test`) en un contenedor propio (`postgres_ventas`, PostgreSQL 16) en el puerto 5435, distinto del 5434 que ocupaba otro servidor local. No tocan datos reales.
- Cada prueba parte de un estado conocido: `BaseDeDatosDePruebas.limpiar` ejecuta `TRUNCATE TABLE detalle_venta, ventas, productos RESTART IDENTITY` al **inicio** de cada prueba.
- `verify-module.sh M11` recrea todo desde cero: `docker compose down -v`, `up`, `DROP SCHEMA public CASCADE`, esquema desde `schema.sql` y comprobación de tablas.
- Las comprobaciones del estado final (encabezado, detalles y existencias) se hacen por una conexión JDBC independiente del adaptador bajo prueba, también para Hibernate.
- Las pruebas del controlador Swing usan repositorios en memoria y no tocan la base de datos.
- La conexión se configura por variables de entorno (`DB_URL`, `DB_USER`, `DB_PASSWORD`, `DB_PORT`); no hay credenciales en el código.

**Límites conocidos**
- Como el `TRUNCATE` ocurre al inicio, los datos de la última prueba permanecen en la base hasta la siguiente ejecución. Para la demostración manual se usa `scripts/datos-demo.sql`, que vacía y carga datos conocidos.
- Las pruebas no son seguras para ejecución en paralelo contra la misma base: comparten tablas.
- Se prueba contra PostgreSQL real, no contra una base en memoria. Requieren Docker en ejecución y `DB_PASSWORD` definida.
- `.env.example` incluye valores solo para uso local de prueba.
