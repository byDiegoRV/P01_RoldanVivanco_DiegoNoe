# P08: Capa de Persistencia JDBC y Arquitectura Hexagonal

## 📋 Resumen del Hito
Implementación y validación de la capa de persistencia mediante JDBC en el sistema de ventas, conectando la arquitectura hexagonal con una base de datos relacional PostgreSQL 16 contenerizada, asegurando el cumplimiento de pruebas unitarias/integración y los estándares de calidad de código con SonarQube.

---

## 🛠️ 1. Implementación del Adaptador de Persistencia (`PostgresProductoAdapter`)
Se desarrolló el adaptador de infraestructura implementando el puerto de salida `ProductoRepository`:
* **Manejo Transaccional:** Uso de bloques `try-with-resources` para la gestión segura de conexiones, desactivación temporal de auto-commit (`conn.setAutoCommit(false)`), confirmación explícita (`conn.commit()`) y reversión ante fallos (`conn.rollback()`).
* **Autoincremento de IDs:** Configuración de la consulta de inserción con recuperación de llaves generadas mediante `Statement.RETURN_GENERATED_KEYS` para asignar el identificador autoincremental provisto por la base de datos (`SERIAL PRIMARY KEY`).
* **Mantenibilidad:** Refactorización orientada a buenas prácticas, eliminando bloques anidados y delegando la ejecución en métodos privados limpios.

---

## 🗄️ 2. Esquema de Base de Datos (PostgreSQL 16)
La tabla `productos` se estructuró en el servidor PostgreSQL (ejecutándose en el contenedor Docker, puerto `5434`, base de datos `ventas_test`) con el siguiente script de esquema (`schema.sql`):

```sql
CREATE TABLE IF NOT EXISTS productos (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    precio NUMERIC(10, 2) NOT NULL,
    stock INT NOT NULL
);