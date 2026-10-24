package mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarritoViewTest {

    private CarritoView carritoView;

    @BeforeEach
    void setUp() {
        carritoView = new CarritoView();
    }

    @Test
    void debeAgregarProductoYAgruparCantidadCorrectamente() {
        carritoView.agregarProducto("Monitor", 3200.0);
        carritoView.agregarProducto("Monitor", 3200.0); // Debe agrupar e incrementar a x2

        assertEquals(6400.0, carritoView.getTotalGeneral(), 0.001);
        assertEquals(1, carritoView.getModeloLista().size(), "Debe mostrarse en un solo renglón agrupado");
        assertTrue(carritoView.getModeloLista().get(0).contains("(x2)"));
    }

    @Test
    void debePermitirQuitarProductoSeleccionado() {
        carritoView.agregarProducto("Teclado", 500.0);
        assertEquals(500.0, carritoView.getTotalGeneral(), 0.001);

        // Seleccionamos el primer elemento de la lista simulando la interacción
        carritoView.getListaCarrito().setSelectedIndex(0);
        carritoView.quitarProductoSeleccionado();

        assertEquals(0.0, carritoView.getTotalGeneral(), 0.001);
        assertTrue(carritoView.getModeloLista().isEmpty(), "El carrito debe quedar vacío tras eliminar");
    }

    @Test
    void debeLimpiarTodoElCarritoCorrectamente() {
        carritoView.agregarProducto("Mouse", 250.0);
        carritoView.agregarProducto("Audífonos", 800.0);
        
        assertEquals(1050.0, carritoView.getTotalGeneral(), 0.001);

        carritoView.limpiarCarrito();

        assertEquals(0.0, carritoView.getTotalGeneral(), 0.001);
        assertTrue(carritoView.getModeloLista().isEmpty());
    }

    @Test
    void debeIncrementarYDecrementarCantidadProducto() {
        carritoView.agregarProducto("Teclado", 300.0);
        carritoView.getListaCarrito().setSelectedIndex(0);
        
        // Validamos que las operaciones de incremento y decremento se ejecuten correctamente
        assertDoesNotThrow(() -> {
            carritoView.incrementarCantidadSeleccionada();
            carritoView.decrementarCantidadSeleccionada();
        });
    }

    @Test
    void debeValidarGettersDeControlesEnCarritoView() {
        assertNotNull(carritoView.getBtnIncrementar());
        assertNotNull(carritoView.getBtnDecrementar());
        assertNotNull(carritoView.getBtnQuitar());
        assertNotNull(carritoView.getListaCarrito());
    }
}