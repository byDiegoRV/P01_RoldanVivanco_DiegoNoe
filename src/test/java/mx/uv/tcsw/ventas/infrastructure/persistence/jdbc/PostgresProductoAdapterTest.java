package mx.uv.tcsw.ventas.infrastructure.persistence.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.domain.Producto;
import mx.uv.tcsw.ventas.infrastructure.persistence.BaseDeDatosDePruebas;

class PostgresProductoAdapterTest {

    private PostgresProductoAdapter productoAdapter;
    private DatabaseConnectionManager connectionManager;

    @BeforeEach
       void setUp() throws SQLException {
       connectionManager = BaseDeDatosDePruebas.crearConexion();
       productoAdapter = new PostgresProductoAdapter(connectionManager);
       BaseDeDatosDePruebas.limpiar(connectionManager);
   }

    @Test
    void debeGuardarYBuscarProductoExitosamente() {
        Producto producto = new Producto("P-001", "Laptop Test", 15000.0, 10);
        productoAdapter.guardar(producto);

        assertNotNull(producto.getId());
        
        Optional<Producto> encontrado = productoAdapter.buscarPorId(producto.getId());
        assertTrue(encontrado.isPresent());
        assertEquals("P-001", encontrado.get().getCodigo());
        assertEquals("Laptop Test", encontrado.get().getNombre());
        assertEquals(15000.0, encontrado.get().getPrecio());
        assertEquals(10, encontrado.get().getExistencia());
    }

    @Test
    void debeGuardarMultiplesProductosConIdsDiferenciados() {
        Producto prod1 = new Producto("PROD-A", "Mouse", 500.0, 25);
        Producto prod2 = new Producto("PROD-B", "Teclado", 1200.0, 15);

        productoAdapter.guardar(prod1);
        productoAdapter.guardar(prod2);

        assertNotNull(prod1.getId());
        assertNotNull(prod2.getId());
        assertNotEquals(prod1.getId(), prod2.getId());

        assertTrue(productoAdapter.buscarPorId(prod1.getId()).isPresent());
        assertTrue(productoAdapter.buscarPorId(prod2.getId()).isPresent());
    }

    @Test
    void debeRetornarOptionalVacioSiElIdNoExiste() {
        Optional<Producto> resultado = productoAdapter.buscarPorId(999999);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeRetornarOptionalVacioSiElCodigoNoExiste() {
        Optional<Producto> resultado = productoAdapter.buscarPorCodigo("COD-FALSO-999");
        assertTrue(resultado.isEmpty(), "Debe retornar Optional vacío si el código no está registrado");
    }

    @Test
    void debeLanzarExcepcionAnteErrorDeInsercionPorDatosNulos() {
    assertThrows(IllegalArgumentException.class, () -> {
        new Producto(null, null, -50.0, -1);
    });
}

    @Test
    void debeLanzarExcepcionSiCodigoExcedeLongitudMaxima() {
        String codigoLargo = "A".repeat(100);
        Producto productoExcesivo = new Producto(codigoLargo, "Producto Largo", 100.0, 5);
        assertThrows(IllegalStateException.class, () -> {
            productoAdapter.guardar(productoExcesivo);
        });
    }

    @Test
    void debeListarTodosLosProductosGuardados() {
        Producto prod1 = new Producto("LST-1", "Monitor", 3000.0, 8);
        Producto prod2 = new Producto("LST-2", "Audífonos", 800.0, 20);
        
        productoAdapter.guardar(prod1);
        productoAdapter.guardar(prod2);

        List<Producto> productos = productoAdapter.listarTodos();
        assertNotNull(productos);
        assertTrue(productos.size() >= 2);
    }

   @Test
    void debeActualizarStockCorrectamente() {
        Producto producto = new Producto("STK-01", "Smartphone", 8000.0, 10);
        productoAdapter.guardar(producto);

        // Creamos una instancia con el ID generado y el stock actualizado
        Producto productoActualizado = new Producto(producto.getId(), "STK-01", "Smartphone", 8000.0, 7);
        productoAdapter.actualizar(productoActualizado);

        Optional<Producto> actualizado = productoAdapter.buscarPorId(producto.getId());
        assertTrue(actualizado.isPresent());
        assertEquals(7, actualizado.get().getExistencia(), "El stock debió actualizarse a 7 en la base de datos");
    }

    @Test
    void debeEliminarProductoCorrectamente() {
        Producto producto = new Producto("DEL-01", "Impresora", 2500.0, 4);
        productoAdapter.guardar(producto);
        
        Integer idGenerado = producto.getId();
        assertNotNull(idGenerado);

        productoAdapter.eliminar(idGenerado);

        Optional<Producto> eliminado = productoAdapter.buscarPorId(idGenerado);
        assertTrue(eliminado.isEmpty());
    }

    @Test
    void debeBuscarProductoPorCodigoExitosamente() {
        String codigoUnico = "CODE-999";
        Producto producto = new Producto(codigoUnico, "Tablet Pro", 6000.0, 12);
        productoAdapter.guardar(producto);

        Optional<Producto> encontrado = productoAdapter.buscarPorCodigo(codigoUnico);
        assertTrue(encontrado.isPresent());
        assertEquals("Tablet Pro", encontrado.get().getNombre());
        assertEquals(6000.0, encontrado.get().getPrecio());
    }

    @Test
    void debePreservarPrecisionDecimalEnPrecio() {
        Producto producto = new Producto("DEC-01", "Licuadora", 1299.99, 5);
        productoAdapter.guardar(producto);

        Optional<Producto> recuperado = productoAdapter.buscarPorId(producto.getId());
        assertTrue(recuperado.isPresent());
        assertEquals(1299.99, recuperado.get().getPrecio(), 0.001);
    }

    @Test
    void debePermitirGuardarProductoConStockEnCero() {
        Producto producto = new Producto("ZERO-01", "Sin Existencia", 150.0, 0);
        productoAdapter.guardar(producto);

        assertNotNull(producto.getId());
        Optional<Producto> recuperado = productoAdapter.buscarPorId(producto.getId());
        assertTrue(recuperado.isPresent());
        assertEquals(0, recuperado.get().getExistencia(), "Debe permitir stock en cero para inventario agotado");
    }
}