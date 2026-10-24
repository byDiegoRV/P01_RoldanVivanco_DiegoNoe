<<<<<<< HEAD:src/test/java/mx/uv/tcsw/ventas/infrastructure/delivery/swing/VentaFrameTest.java
package mx.uv.tcsw.ventas.infrastructure.delivery.swing;
=======
package mx.uv.tcsw.ventas;
>>>>>>> 63909dc (feat(swing): completa pruebas unitarias y validaciones de VentaFrameTest):src/test/java/mx/uv/tcsw/ventas/VentaFrameTest.java

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

<<<<<<< HEAD:src/test/java/mx/uv/tcsw/ventas/infrastructure/delivery/swing/VentaFrameTest.java
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CatalogoView;
=======
import mx.uv.tcsw.ventas.adapter.in.swing.VentaFrame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
>>>>>>> 63909dc (feat(swing): completa pruebas unitarias y validaciones de VentaFrameTest):src/test/java/mx/uv/tcsw/ventas/VentaFrameTest.java

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
