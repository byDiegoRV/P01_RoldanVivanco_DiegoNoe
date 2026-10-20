package mx.uv.tcsw.ventas.infrastructure.persistence.jdbc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.domain.Producto;

class PostgresProductoAdapterTest {

    private PostgresProductoAdapter productoAdapter;
    private DatabaseConnectionManager connectionManager;

    @BeforeEach
    void setUp() {
        // Configuración de la conexión al contenedor PostgreSQL con el puerto 5434
        connectionManager = new DatabaseConnectionManager(
            "jdbc:postgresql://localhost:5434/ventas_test", 
            "postgres", 
            "1234"
        );
        productoAdapter = new PostgresProductoAdapter(connectionManager);
    }

    @Test
    void debeGuardarYBuscarProductoExitosamente() {
        // Arrange: Se crea un producto sin ID; la base de datos se encarga de generarlo de forma autoincremental
        Producto producto = new Producto("P-001", "Laptop Test", 15000.00, 10);

        // Act: Guardar en la base de datos
        productoAdapter.guardar(producto);

        // Validar que se haya asignado un ID automáticamente al objeto tras el guardado
        assertNotNull(producto.getId(), "El ID autoincrementable no debe ser nulo tras guardarse en la BD");

        // Buscar el producto utilizando el ID generado
        Optional<Producto> encontrado = productoAdapter.buscarPorId(producto.getId());

        // Assert: Verificar la integridad de los datos recuperados
        assertTrue(encontrado.isPresent());
        assertEquals(producto.getId(), encontrado.get().getId());
        assertEquals("P-001", encontrado.get().getCodigo());
        assertEquals("Laptop Test", encontrado.get().getNombre());
        assertEquals(15000.00, encontrado.get().getPrecio());
        assertEquals(10, encontrado.get().getExistencia());
    }
}