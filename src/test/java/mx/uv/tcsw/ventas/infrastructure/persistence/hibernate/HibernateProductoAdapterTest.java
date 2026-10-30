package mx.uv.tcsw.ventas.infrastructure.persistence.hibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.domain.Producto;
import mx.uv.tcsw.ventas.infrastructure.persistence.BaseDeDatosDePruebas;

/** El adaptador Hibernate cumple el mismo puerto ProductoRepository que el adaptador JDBC. */
class HibernateProductoAdapterTest {

    private SessionFactory sessionFactory;
    private HibernateProductoAdapter productos;

    @BeforeEach
    void setUp() throws SQLException {
        BaseDeDatosDePruebas.limpiar(BaseDeDatosDePruebas.crearConexion());
        sessionFactory = HibernateSessionFactoryProvider.crear(
            BaseDeDatosDePruebas.url(), BaseDeDatosDePruebas.usuario(), BaseDeDatosDePruebas.password());
        productos = new HibernateProductoAdapter(sessionFactory);
    }

    @AfterEach
    void tearDown() {
        sessionFactory.close();
    }

    @Test
    void guardaYBuscaPorIdYPorCodigo() {
        Producto laptop = new Producto("P-001", "Laptop", 15000.50, 10);
        productos.guardar(laptop);

        assertNotNull(laptop.getId());
        Optional<Producto> porId = productos.buscarPorId(laptop.getId());
        assertTrue(porId.isPresent());
        assertEquals("P-001", porId.get().getCodigo());
        assertEquals(15000.50, porId.get().getPrecio(), 0.001);
        assertTrue(productos.buscarPorCodigo("P-001").isPresent());
        assertFalse(productos.buscarPorCodigo("NO-EXISTE").isPresent());
    }

    @Test
    void actualizaStockYListaTodos() {
        Producto laptop = new Producto("P-001", "Laptop", 15000.0, 10);
        Producto mouse = new Producto("P-002", "Mouse", 250.0, 5);
        productos.guardar(laptop);
        productos.guardar(mouse);

        productos.actualizar(new Producto(laptop.getId(), "P-001", "Laptop", 15000.0, 7));

        assertEquals(2, productos.listarTodos().size());
        assertEquals(7, productos.buscarPorId(laptop.getId()).orElseThrow().getExistencia());
    }

    @Test
    void eliminaProducto() {
        Producto laptop = new Producto("P-001", "Laptop", 15000.0, 10);
        productos.guardar(laptop);

        productos.eliminar(laptop.getId());

        assertFalse(productos.buscarPorId(laptop.getId()).isPresent());
    }
}
