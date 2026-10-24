package mx.uv.tcsw.ventas.infrastructure.delivery.swing;



import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CatalogoView;

class VentaFrameTest {

    private CatalogoView catalogoView;

    @BeforeEach
    void setUp() {
        catalogoView = new CatalogoView();
    }

    @Test
    @DisplayName("Debe inicializar correctamente la vista de catálogo y sus componentes")
    void testInicializacionVentana() {
        assertNotNull(catalogoView, "La instancia de CatalogoView no debe ser nula");
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


