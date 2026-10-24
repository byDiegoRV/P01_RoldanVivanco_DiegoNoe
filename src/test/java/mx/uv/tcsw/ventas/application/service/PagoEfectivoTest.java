package mx.uv.tcsw.ventas.application.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

class PagoEfectivoTest {

    @Test
    void debeProcesarPagoEfectivoCorrectamente() {
        PagoEfectivo pago = new PagoEfectivo();
        double monto = 500.0;
        
        // Como el método es void, validamos que se ejecute de forma exitosa sin lanzar excepciones
        assertDoesNotThrow(() -> pago.procesarPago(monto));
    }
}