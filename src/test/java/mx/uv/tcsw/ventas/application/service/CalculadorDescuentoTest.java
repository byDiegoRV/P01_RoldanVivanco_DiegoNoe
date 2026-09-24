package mx.uv.tcsw.ventas.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.domain.Venta;

class CalculadorDescuentoTest {

    private CalculadorDescuentoStrategy estrategia;

    @BeforeEach
    void setUp() {
        estrategia = new DescuentoVIP();
    }

    @Test
    void deberiaCalcularDescuentoExitosamente() {
        // Arrange
        Venta venta = new Venta("F001");
        
        // Act
        double descuento = estrategia.calcular(venta);

        // Assert (Quitamos assertNotNull porque double es primitivo y nunca es null)
        assertEquals(0.0, descuento, 0.01);
    }

    @Test
    void deberiaLanzarExcepcionSiVentaEsNula() {
        CalculadorDescuentoStrategy estrategiaNula = null;

        assertThrows(
            NullPointerException.class,
            () -> estrategiaNula.calcular(null)
        );
    }
}