package mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class FormularioVentaViewTest {

    @Test
    void testInicializacionFormulario() {
        FormularioVentaView vista = new FormularioVentaView();
        assertNotNull(vista);
        assertNotNull(vista.getTxtNombre());
        assertNotNull(vista.getTxtTarjeta());
        assertNotNull(vista.getBtnConfirmar());
        assertNotNull(vista.getBtnCancelar());
    }
}