package mx.uv.tcsw.ventas;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class VentaTest {

    @Test
    void crearVentaValida() {
        Venta venta = new Venta("V-001");
        assertEquals("V-001", venta.getFolio());
        assertTrue(venta.getDetalles().isEmpty());
    }

    @Test
    void folioNuloOEnBlancoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Venta(null));
        assertThrows(IllegalArgumentException.class, () -> new Venta(""));
        assertThrows(IllegalArgumentException.class, () -> new Venta("   "));
    }

    @Test
    void agregarPartidaValidaDescuentaYRegistraDetalle() {
        Producto producto = new Producto("P01", "Cuaderno", 25.0, 10);
        Venta venta = new Venta("V-002");

        venta.agregarPartida(producto, 3);

        assertEquals(1, venta.getDetalles().size());
        assertEquals(7, producto.getExistencia());
        assertEquals(75.0, venta.calcularTotal());
    }

    @Test
    void agregarPartidaProductoNuloLanzaExcepcion() {
        Venta venta = new Venta("V-003");
        assertThrows(IllegalArgumentException.class, () -> venta.agregarPartida(null, 2));
    }

    @Test
    void agregarPartidaCantidadExcedeExistenciaLanzaExcepcion() {
        Producto producto = new Producto("P02", "Lapicera", 50.0, 2);
        Venta venta = new Venta("V-004");

        assertThrows(IllegalStateException.class, () -> venta.agregarPartida(producto, 5));
        assertEquals(2, producto.getExistencia());
        assertTrue(venta.getDetalles().isEmpty());
    }

    @Test
    void calcularTotalVaciaDevuelveCero() {
        Venta venta = new Venta("V-005");
        assertEquals(0.0, venta.calcularTotal());
    }

    @Test
    void calcularTotalMultiplesPartidas() {
        Producto p1 = new Producto("P01", "Libreta", 20.0, 10);
        Producto p2 = new Producto("P02", "Pluma", 10.0, 20);
        Venta venta = new Venta("V-006");

        venta.agregarPartida(p1, 2); // 40.0
        venta.agregarPartida(p2, 3); // 30.0

        assertEquals(70.0, venta.calcularTotal());
    }

    @Test
    void equalsMismoFolioMismaVenta() {
        Venta v1 = new Venta("V-007");
        Venta v2 = new Venta("V-007");
        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    void toStringContieneInformacionBasica() {
        Venta venta = new Venta("V-008");
        String texto = venta.toString();
        assertTrue(texto.contains("V-008"));
        assertTrue(texto.contains("detalles=0"));
    }

    @Test
    void getDetallesNoPuedeModificarseDesdeAfuera() {
        Venta venta = new Venta("V-009");
        List<DetalleVenta> detalles = venta.getDetalles();
        DetalleVenta detalleExterno = new DetalleVenta(
                new Producto("P09", "Borrador", 5.0, 10), 1, 5.0
        );

        assertThrows(UnsupportedOperationException.class,
                () -> detalles.add(detalleExterno));
    }

    @Test
    void testEliminarPartida() {
        Venta venta = new Venta("V-012");
        Producto p = new Producto("P01", "Cuaderno", 25.0, 10);
        venta.agregarPartida(p, 2);
        DetalleVenta detalle = venta.getDetalles().get(0);

        assertTrue(venta.eliminarPartida(detalle));
        assertTrue(venta.getDetalles().isEmpty());
        assertFalse(venta.eliminarPartida(null));
        assertFalse(venta.eliminarPartida(detalle));
    }

    @Test
    void testTotalFormateado() {
        Venta venta = new Venta("V-013");
        Producto p = new Producto("P01", "Cuaderno", 25.50, 10);
        venta.agregarPartida(p, 2);
        assertEquals("51.00", venta.totalFormateado());
    }
}
