CREATE TABLE IF NOT EXISTS productos (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    precio NUMERIC(10, 2) NOT NULL,
    stock INT NOT NULL
);

CREATE TABLE IF NOT EXISTS ventas (
    id SERIAL PRIMARY KEY,
    folio VARCHAR(50) NOT NULL UNIQUE,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total NUMERIC(12, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS detalle_venta (
    id SERIAL PRIMARY KEY,
    venta_id INT NOT NULL REFERENCES ventas(id),
    producto_id INT NOT NULL REFERENCES productos(id),
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10, 2) NOT NULL
);