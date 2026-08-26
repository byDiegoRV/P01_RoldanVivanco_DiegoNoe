package mx.uv.tcsw.ventas;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Pruebas automatizadas para la entidad Venta.
 *
 * <p>Incluye casos positivos (agregar partidas, calcular total), casos
 * negativos o limite (folio invalido, producto nulo, cantidad invalida,
 * existencia insuficiente) y una prueba de preservacion de estado: un
 * rechazo al agregar una partida no debe dejar la venta ni el producto
 * parcialmente modificados.</p>
 */
class VentaTest {

    private static Producto productoValido() {
        return new Producto("P-001", "Teclado mecanico", 899.90, 10);
    }

    // ---------- Casos positivos ----------

    @Test
    void creaVentaVaciaConTotalCero() {
        Venta venta = new Venta("V-001");

        assertEquals("V-001", venta.getFolio());
        assertTrue(venta.getDetalles().isEmpty());
        assertEquals(0.0, venta.calcularTotal());
    }

    @Test
    void agregarPartidaDescuentaExistenciaDelProducto() {
        Producto producto = productoValido();
        Venta venta = new Venta("V-002");

        venta.agregarPartida(producto, 3);

        assertEquals(7, producto.getExistencia());
    }

    @Test
    void agregarPartidaRegistraUnDetalleConPrecioCongelado() {
        Producto producto = productoValido();
        Venta venta = new Venta("V-003");

        venta.agregarPartida(producto, 2);

        assertEquals(1, venta.getDetalles().size());
        DetalleVenta detalle = venta.getDetalles().get(0);
        assertEquals(producto, detalle.getProducto());
        assertEquals(2, detalle.getCantidad());
        assertEquals(899.90, detalle.getPrecioUnitario());
    }

    @Test
    void calcularTotalSumaLosSubtotalesDeVariasPartidas() {
        Producto teclado = new Producto("P-001", "Teclado", 900.0, 10);
        Producto mouse = new Producto("P-002", "Mouse", 300.0, 10);
        Venta venta = new Venta("V-004");

        venta.agregarPartida(teclado, 2); // 1800.0
        venta.agregarPartida(mouse, 3);   // 900.0

        assertEquals(2700.0, venta.calcularTotal());
    }

    @Test
    void elPrecioDeLaPartidaNoCambiaAunqueElProductoCambieDespues() {
        Producto producto = productoValido();
        Venta venta = new Venta("V-005");

        venta.agregarPartida(producto, 1);
        producto.actualizarPrecio(1500.0);

        assertEquals(899.90, venta.calcularTotal());
    }

    @Test
    void dosVentasConElMismoFolioSonIguales() {
        Venta uno = new Venta("V-006");
        Venta otro = new Venta("V-006");

        assertEquals(uno, otro);
        assertEquals(uno.hashCode(), otro.hashCode());
    }

    // ---------- Casos negativos / limite ----------

    @Test
    void rechazaFolioNuloAlCrear() {
        assertThrows(IllegalArgumentException.class, () -> new Venta(null));
    }

    @Test
    void rechazaFolioEnBlancoAlCrear() {
        assertThrows(IllegalArgumentException.class, () -> new Venta("   "));
    }

    @Test
    void rechazaAgregarPartidaConProductoNulo() {
        Venta venta = new Venta("V-007");

        assertThrows(IllegalArgumentException.class, () -> venta.agregarPartida(null, 1));
    }

    @Test
    void rechazaAgregarPartidaConCantidadCeroOMenor() {
        Producto producto = productoValido();
        Venta venta = new Venta("V-008");

        assertThrows(IllegalArgumentException.class, () -> venta.agregarPartida(producto, 0));
    }

    @Test
    void rechazaAgregarPartidaConCantidadMayorALaExistencia() {
        Producto producto = productoValido(); // existencia: 10
        Venta venta = new Venta("V-009");

        assertThrows(IllegalStateException.class, () -> venta.agregarPartida(producto, 999));
    }

    // ---------- Preservacion de estado tras un rechazo ----------

    @Test
    void unaPartidaRechazadaNoModificaLaExistenciaNiLaVenta() {
        Producto producto = productoValido(); // existencia: 10
        Venta venta = new Venta("V-010");

        assertThrows(IllegalStateException.class, () -> venta.agregarPartida(producto, 999));

        assertEquals(10, producto.getExistencia(),
                "La existencia del producto no debe cambiar si la partida fue rechazada.");
        assertTrue(venta.getDetalles().isEmpty(),
                "La venta no debe registrar un detalle si la partida fue rechazada.");
    }

@Test
void getDetallesNoPuedeModificarseDesdeAfuera() {
    Producto producto = productoValido();
    Venta venta = new Venta("V-011");
    venta.agregarPartida(producto, 1);
    DetalleVenta detalleExterno = new DetalleVenta(producto, 1, 100.0);
    List<DetalleVenta> detalles = venta.getDetalles();

    assertThrows(UnsupportedOperationException.class,
            () -> detalles.add(detalleExterno));
}
}
