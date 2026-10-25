package mx.uv.tcsw.ventas.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.domain.Venta;

class DescuentoSinPromoTest {

    @Test
    void debeRetornarMontoSinDescuento() {
        Venta venta = new Venta("VENTA-01"); 
        DescuentoSinPromo descuento = new DescuentoSinPromo();
        
        double precioFinal = descuento.calcular(venta); 
        
        
        assertEquals(0.0, precioFinal, 0.001);
    }
}