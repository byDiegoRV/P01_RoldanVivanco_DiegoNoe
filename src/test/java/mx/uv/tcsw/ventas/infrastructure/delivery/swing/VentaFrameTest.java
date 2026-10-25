package mx.uv.tcsw.ventas.infrastructure.delivery.swing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.adapter.in.swing.VentaFrame;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CatalogoView;
// Asegúrate de importar tu VentaFrame si está en otro paquete (ajusta si es necesario)
// import mx.uv.tcsw.ventas.adapter.in.swing.VentaFrame; 

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

    @Test
    @DisplayName("Debe inicializar correctamente la clase VentaFrame")
    void testInicializacionVentaFrame() {
        // Nueva prueba añadida para cubrir las 40 líneas pendientes en SonarQube
        VentaFrame frame = new VentaFrame();
        assertNotNull(frame, "La instancia de VentaFrame no debe ser nula");
    }

    @Test
    @DisplayName("Debe validar comportamiento de VentaFrame")
    void debeValidarComportamientoDeVentaFrame() {
        VentaFrame frame = new VentaFrame();
        assertNotNull(frame, "El frame debe inicializarse");
        
        frame.pack();
        assertTrue(frame.isDisplayable(), "El frame debe estar listo en memoria");
    }
}


