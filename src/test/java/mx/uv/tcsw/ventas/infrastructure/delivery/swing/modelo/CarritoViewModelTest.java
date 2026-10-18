package mx.uv.tcsw.ventas.infrastructure.delivery.swing.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CarritoViewModelTest {

    @Test
    void testCarritoViewModelGetters() {
        CarritoViewModel carrito = new CarritoViewModel("Mouse", 2, 500.0);
        
        assertEquals("Mouse", carrito.getNombreProducto());
        assertEquals(2, carrito.getCantidad());
        assertEquals(500.0, carrito.getSubtotal());
    }
}
