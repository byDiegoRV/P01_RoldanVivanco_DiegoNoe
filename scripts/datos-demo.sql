-- Datos de demostración para el recorrido de C02 (no contiene datos reales).
-- Deja las tablas en un estado conocido y carga el catálogo inicial.
TRUNCATE TABLE detalle_venta, ventas, productos RESTART IDENTITY;

INSERT INTO productos (codigo, nombre, precio, stock) VALUES
 ('P-001', 'Laptop X',           15000.00, 10),
 ('P-002', 'Teclado Mecánico',    1200.00, 25),
 ('P-003', 'Mouse Inalámbrico',    450.00,  0);
