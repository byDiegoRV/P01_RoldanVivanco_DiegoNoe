package mx.uv.tcsw.ventas.application.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PagoFactoryTest {

    @Test
    void deberiaRetornarPagoEfectivoExitosamente() {
        // Arrange (Caso positivo)
        String tipo = "EFECTIVO";

        // Act
        MetodoPago metodo = PagoFactory.obtenerMetodoPago(tipo);

        // Assert
        assertNotNull(metodo);
        assertTrue(metodo instanceof PagoEfectivo);
    }

    @Test
    void deberiaLanzarExcepcionSiTipoEsInvalidoONulo() {
        // Arrange, Act & Assert (Casos negativos / límites)
        assertThrows(
            IllegalArgumentException.class,
            () -> PagoFactory.obtenerMetodoPago("TARJETA_INEXISTENTE")
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> PagoFactory.obtenerMetodoPago(null)
        );
    }
}