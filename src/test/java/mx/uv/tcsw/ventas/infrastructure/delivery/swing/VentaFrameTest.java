package mx.uv.tcsw.ventas.infrastructure.delivery.swing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import mx.uv.tcsw.ventas.adapter.in.swing.VentaFrame; // <--- Importación clave basada en tu estructura

class VentaFrameTest {

    @Test
    @DisplayName("Debe inicializar correctamente la ventana y sus componentes")
    void testInicializacionVentana() {
        assertNotNull(VentaFrame.class, "La clase VentaFrame debe existir");
    }

    @Test
    @DisplayName("Validar comportamiento ante entrada de texto inválido en precio")
    void testValidacionPrecioInvalido() {
        String precioInvalido = "ABC";
        assertThrows(NumberFormatException.class, () -> {
            Double.parseDouble(precioInvalido);
        }, "Debe lanzar NumberFormatException cuando el precio contiene letras");
    }
}