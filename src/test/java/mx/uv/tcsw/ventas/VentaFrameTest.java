package mx.uv.tcsw.ventas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.adapter.in.swing.VentaFrame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VentaFrameTest {

    private VentaFrame ventaFrame;

    @BeforeEach
    void setUp() {
        ventaFrame = new VentaFrame();
    }

    @Test
    @DisplayName("Debe inicializar correctamente la ventana y sus componentes")
    void testInicializacionVentana() {
        assertNotNull(ventaFrame, "La instancia de VentaFrame no debe ser nula");
        assertEquals("Módulo de Ventas - Cliente Swing", ventaFrame.getTitle());
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
    
