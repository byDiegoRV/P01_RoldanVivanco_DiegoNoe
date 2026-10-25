package mx.uv.tcsw.ventas.infrastructure.delivery.swing.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.domain.Producto;

class CarritoViewModelTest {

    @Test
    void debeInicializarConSeleccionPorDefectoYCantidadValida() {
        Producto producto = new Producto("PROD-01", "Monitor", 3000.0, 10);
        CarritoViewModel item = new CarritoViewModel(producto, 2);

        assertEquals(producto, item.getProducto());
        assertEquals(2, item.getCantidad());
        assertTrue(item.isSeleccionado(), "Por defecto el producto debe estar seleccionado para el pago");
        assertEquals(6000.0, item.getSubtotal(), 0.001);
    }

    @Test
    void debePermitirModificarCantidadYCalcularSubtotalCorrectamente() {
        Producto producto = new Producto("PROD-02", "Teclado", 500.0, 15);
        CarritoViewModel item = new CarritoViewModel(producto, 1);

        item.setCantidad(4);
        assertEquals(4, item.getCantidad());
        assertEquals(2000.0, item.getSubtotal(), 0.001);
    }

    @Test
    void noDebePermitirCantidadesMenoresAUno() {
        Producto producto = new Producto("PROD-03", "Mouse", 300.0, 20);
        CarritoViewModel item = new CarritoViewModel(producto, 3);

        item.setCantidad(0); // Intentamos poner cero o negativo
        assertEquals(1, item.getCantidad(), "La cantidad mínima siempre debe ser al menos 1");
        assertEquals(300.0, item.getSubtotal(), 0.001);
    }

    @Test
    void debePermitirMarcarYDesmarcarSeleccion() {
        Producto producto = new Producto("PROD-04", "Audífonos", 800.0, 10);
        CarritoViewModel item = new CarritoViewModel(producto, 1);

        assertTrue(item.isSeleccionado());

        item.setSeleccionado(false);
        assertFalse(item.isSeleccionado(), "El producto debe quedar desmarcado estilo Mercado Libre");
    }
}