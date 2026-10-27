package mx.uv.tcsw.ventas.infrastructure.persistence.hibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.domain.Producto;
import mx.uv.tcsw.ventas.domain.Venta;
import mx.uv.tcsw.ventas.infrastructure.persistence.BaseDeDatosDePruebas;
import mx.uv.tcsw.ventas.infrastructure.persistence.jdbc.DatabaseConnectionManager;

/**
 * Mismos tres casos que VentaJdbcRepositoryTest, ahora contra el adaptador Hibernate.
 * El estado final se verifica por JDBC, de forma independiente al adaptador probado.
 */
class HibernateVentaAdapterTest {

    private DatabaseConnectionManager jdbc;
    private SessionFactory sessionFactory;
    private HibernateProductoAdapter productos;
    private HibernateVentaAdapter ventas;

    @BeforeEach
    void setUp() throws SQLException {
        jdbc = BaseDeDatosDePruebas.crearConexion();
        BaseDeDatosDePruebas.limpiar(jdbc);
        sessionFactory = HibernateSessionFactoryProvider.crear(
            BaseDeDatosDePruebas.url(), BaseDeDatosDePruebas.usuario(), BaseDeDatosDePruebas.password());
        productos = new HibernateProductoAdapter(sessionFactory);
        ventas = new HibernateVentaAdapter(sessionFactory);
    }

    @AfterEach
    void tearDown() {
        sessionFactory.close();
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

        assertEquals(1, BaseDeDatosDePruebas.contar(jdbc, "ventas"));
        assertEquals(2, BaseDeDatosDePruebas.contar(jdbc, "detalle_venta"));
        assertEquals(8, BaseDeDatosDePruebas.stockDe(jdbc, laptop.getId()));
        assertEquals(4, BaseDeDatosDePruebas.stockDe(jdbc, mouse.getId()));
    }

    @Test
    void existenciaInsuficienteEnBdNoDejaVentaNiCambiaStock() throws SQLException {
        Producto laptop = new Producto("P-001", "Laptop", 15000.0, 10);
        productos.guardar(laptop);
        // Otra operación ya redujo el stock real: el objeto en memoria quedó obsoleto
        Producto actualizado = new Producto(laptop.getId(), "P-001", "Laptop", 15000.0, 1);
        productos.actualizar(actualizado);

        Venta venta = new Venta("V-002");
        venta.agregarPartida(laptop, 3);

        assertThrows(IllegalStateException.class, () -> ventas.guardar(venta));

        assertEquals(0, BaseDeDatosDePruebas.contar(jdbc, "ventas"));
        assertEquals(0, BaseDeDatosDePruebas.contar(jdbc, "detalle_venta"));
        assertEquals(1, BaseDeDatosDePruebas.stockDe(jdbc, laptop.getId()));
    }

    @Test
    void fallaIntermediaRevierteEncabezadoDetallesYExistencias() throws SQLException {
        Producto laptop = new Producto("P-001", "Laptop", 15000.0, 10);
        productos.guardar(laptop);
        Producto fantasma = new Producto(9999, "P-999", "No existe en BD", 100.0, 5);

        Venta venta = new Venta("V-003");
        venta.agregarPartida(laptop, 2);
        venta.agregarPartida(fantasma, 1);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> ventas.guardar(venta));

        assertNotNull(ex.getCause()); // la causa original queda disponible
        assertEquals(0, BaseDeDatosDePruebas.contar(jdbc, "ventas"));
        assertEquals(0, BaseDeDatosDePruebas.contar(jdbc, "detalle_venta"));
        assertEquals(10, BaseDeDatosDePruebas.stockDe(jdbc, laptop.getId()));
    }
}
