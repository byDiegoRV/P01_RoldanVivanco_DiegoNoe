package mx.uv.tcsw.ventas.infrastructure.persistence.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.domain.Producto;
import mx.uv.tcsw.ventas.domain.Venta;
import mx.uv.tcsw.ventas.infrastructure.persistence.BaseDeDatosDePruebas;

class VentaJdbcRepositoryTest {

    private DatabaseConnectionManager connectionManager;
    private PostgresProductoAdapter productos;
    private VentaJdbcRepository ventas;

    @BeforeEach
void setUp() throws SQLException {
    connectionManager = BaseDeDatosDePruebas.crearConexion();
    productos = new PostgresProductoAdapter(connectionManager);
    ventas = new VentaJdbcRepository(connectionManager);
    BaseDeDatosDePruebas.limpiar(connectionManager);
}

    @Test
    void ventaValidaConfirmaEncabezadoDetallesYDescuento() throws SQLException {
        Producto laptop = new Producto("P-001", "Laptop", 15000.0, 10);
        Producto mouse = new Producto("P-002", "Mouse", 250.0, 5);
        productos.guardar(laptop);
        productos.guardar(mouse);

        Venta venta = new Venta("V-001");
        venta.agregarPartida(laptop, 2);
        venta.agregarPartida(mouse, 1);
        ventas.guardar(venta);

        assertEquals(1, contar("ventas"));
        assertEquals(2, contar("detalle_venta"));
        assertEquals(8, stockDe(laptop.getId()));
        assertEquals(4, stockDe(mouse.getId()));
    }

    @Test
    void existenciaInsuficienteEnBdNoDejaVentaNiCambiaStock() throws SQLException {
        Producto laptop = new Producto("P-001", "Laptop", 15000.0, 10);
        productos.guardar(laptop);
        // Otra operación ya redujo el stock real: el objeto en memoria quedó obsoleto
        ejecutar("UPDATE productos SET stock = 1 WHERE id = " + laptop.getId());

        Venta venta = new Venta("V-002");
        venta.agregarPartida(laptop, 3);

        assertThrows(IllegalStateException.class, () -> ventas.guardar(venta));

        assertEquals(0, contar("ventas"));
        assertEquals(0, contar("detalle_venta"));
        assertEquals(1, stockDe(laptop.getId()));
    }

    @Test
    void fallaIntermediaRevierteEncabezadoDetallesYExistencias() throws SQLException {
        Producto laptop = new Producto("P-001", "Laptop", 15000.0, 10);
        productos.guardar(laptop);
        Producto fantasma = new Producto(9999, "P-999", "No existe en BD", 100.0, 5);

        Venta venta = new Venta("V-003");
        venta.agregarPartida(laptop, 2);     // primer detalle: se aplica y luego debe revertirse
        venta.agregarPartida(fantasma, 1);   // segundo detalle: viola la clave foránea

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> ventas.guardar(venta));

        assertNotNull(ex.getCause()); // la causa original queda disponible
        assertEquals(0, contar("ventas"));
        assertEquals(0, contar("detalle_venta"));
        assertEquals(10, stockDe(laptop.getId()));
    }

    private int contar(String tabla) throws SQLException {
        try (Connection cn = connectionManager.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private int stockDe(int idProducto) throws SQLException {
        try (Connection cn = connectionManager.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT stock FROM productos WHERE id = " + idProducto)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private void ejecutar(String sql) throws SQLException {
        try (Connection cn = connectionManager.getConnection(); Statement st = cn.createStatement()) {
            st.execute(sql);
        }
    }
}