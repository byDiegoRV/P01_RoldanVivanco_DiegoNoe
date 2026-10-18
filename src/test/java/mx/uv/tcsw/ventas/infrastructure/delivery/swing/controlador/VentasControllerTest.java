package mx.uv.tcsw.ventas.infrastructure.delivery.swing.controlador;

import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.infrastructure.delivery.swing.MainSwing;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CarritoView;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CatalogoView;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.FormularioVentaView;

class VentasControllerTest {

    private CatalogoView catalogoView;
    private CarritoView carritoView;
    private FormularioVentaView formularioView;
    private VentasController controller;

    @BeforeEach
    void setUp() {
        catalogoView = new CatalogoView();
        carritoView = new CarritoView();
        formularioView = new FormularioVentaView();
        controller = new VentasController(catalogoView, carritoView, formularioView);
    }

    @Test
    void testMainSwingExecution() {
        assertDoesNotThrow(() -> MainSwing.main(new String[]{}));
    }

    @Test
    void testVerCarritoAction() {
        ActionEvent event = new ActionEvent(catalogoView.getBtnVerCarrito(), ActionEvent.ACTION_PERFORMED, "command");
        controller.actionPerformed(event);
        assertTrue(carritoView.isVisible());
    }

    @Test
    void testVolverCarritoAction() {
        ActionEvent event = new ActionEvent(carritoView.getBtnVolver(), ActionEvent.ACTION_PERFORMED, "command");
        controller.actionPerformed(event);
        assertFalse(carritoView.isVisible());
    }

    @Test
    void testCancelarFormularioAction() {
        ActionEvent event = new ActionEvent(formularioView.getBtnCancelar(), ActionEvent.ACTION_PERFORMED, "command");
        controller.actionPerformed(event);
        assertFalse(formularioView.isVisible());
    }

    @Test
    void testAgregarProductoSinSeleccion() {
        catalogoView.getTablaProductos().clearSelection();
        ActionEvent event = new ActionEvent(catalogoView.getBtnAgregarCarrito(), ActionEvent.ACTION_PERFORMED, "command");
        assertDoesNotThrow(() -> controller.actionPerformed(event));
    }

    @Test
    void testAgregarProductoConSeleccion() {
        catalogoView.getTablaProductos().setRowSelectionInterval(0, 0);
        ActionEvent event = new ActionEvent(catalogoView.getBtnAgregarCarrito(), ActionEvent.ACTION_PERFORMED, "command");
        controller.actionPerformed(event);
        assertEquals(1, carritoView.getModeloLista().size());
        assertTrue(carritoView.getModeloLista().getElementAt(0).contains("(x1)"));
    }

    @Test
    void testAgrupacionProductoRepetido() {
        // Agregamos el mismo producto (índice 0) dos veces
        catalogoView.getTablaProductos().setRowSelectionInterval(0, 0);
        ActionEvent event = new ActionEvent(catalogoView.getBtnAgregarCarrito(), ActionEvent.ACTION_PERFORMED, "command");
        
        controller.actionPerformed(event);
        controller.actionPerformed(event);

        // Debe mantenerse en 1 solo renglón en la lista pero con cantidad x2
        assertEquals(1, carritoView.getModeloLista().size());
        assertTrue(carritoView.getModeloLista().getElementAt(0).contains("(x2)"));
    }

    @Test
    void testPagarCarritoVacio() {
        ActionEvent event = new ActionEvent(carritoView.getBtnPagar(), ActionEvent.ACTION_PERFORMED, "command");
        controller.actionPerformed(event);
        assertFalse(formularioView.isVisible());
    }

    @Test
    void testPagarConProductosEnCarrito() {
        catalogoView.getTablaProductos().setRowSelectionInterval(0, 0);
        controller.actionPerformed(new ActionEvent(catalogoView.getBtnAgregarCarrito(), ActionEvent.ACTION_PERFORMED, "command"));
        
        ActionEvent event = new ActionEvent(carritoView.getBtnPagar(), ActionEvent.ACTION_PERFORMED, "command");
        controller.actionPerformed(event);
        assertTrue(formularioView.isVisible());
    }

    @Test
    void testValidacionesFormularioGeneral() {
        String[][] casosPrueba = {
            {"", ""},
            {"   ", "1234567890123456"},
            {"12345", "1234567890123456"},
            {"Diego Noe", "123"},
            {"Diego Noe", "1234567890123456"}
        };

        for (String[] caso : casosPrueba) {
            catalogoView.getTablaProductos().setRowSelectionInterval(0, 0);
            controller.actionPerformed(new ActionEvent(catalogoView.getBtnAgregarCarrito(), ActionEvent.ACTION_PERFORMED, "command"));

            formularioView.getTxtNombre().setText(caso[0]);
            formularioView.getTxtTarjeta().setText(caso[1]);

            ActionEvent event = new ActionEvent(formularioView.getBtnConfirmar(), ActionEvent.ACTION_PERFORMED, "command");
            assertDoesNotThrow(() -> controller.actionPerformed(event));
        }
    }
}