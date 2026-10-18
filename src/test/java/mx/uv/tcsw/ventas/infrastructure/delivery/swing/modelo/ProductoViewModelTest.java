package mx.uv.tcsw.ventas.infrastructure.delivery.swing.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductoViewModelTest {

    @Test
    void testProductoViewModelGetters() {
        ProductoViewModel producto = new ProductoViewModel("P01", "Laptop", 15000.0);
        
        assertEquals("P01", producto.getCodigo());
        assertEquals("Laptop", producto.getNombre());
        assertEquals(15000.0, producto.getPrecio());
    }
}