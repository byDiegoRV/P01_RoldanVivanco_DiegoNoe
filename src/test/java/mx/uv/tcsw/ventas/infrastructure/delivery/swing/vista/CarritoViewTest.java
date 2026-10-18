package mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CarritoViewTest {

    @Test
    void testInicializacionYAgrupacionCarrito() {
        CarritoView vista = new CarritoView();
        assertNotNull(vista);
        assertNotNull(vista.getBtnVolver());
        assertNotNull(vista.getBtnPagar());

        // Probar el método de agregar y agrupar productos
        vista.agregarProducto("Laptop X", 15000.0);
        vista.agregarProducto("Laptop X", 15000.0);

        assertEquals(1, vista.getModeloLista().size());
        assertTrue(vista.getModeloLista().getElementAt(0).contains("(x2)"));
        assertEquals(30000.0, vista.getTotalGeneral());

        // Probar limpieza
        vista.limpiarCarrito();
        assertEquals(0, vista.getModeloLista().size());
        assertEquals(0.0, vista.getTotalGeneral());
    }
}