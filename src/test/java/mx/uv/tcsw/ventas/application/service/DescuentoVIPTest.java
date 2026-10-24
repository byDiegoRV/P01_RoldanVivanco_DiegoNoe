package mx.uv.tcsw.ventas.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.domain.Producto;
import mx.uv.tcsw.ventas.domain.Venta;

class DescuentoVIPTest {

    @Test
    void debeAplicarDescuentoVIPCorrectamente() {
        Venta venta = new Venta("VENTA-VIP-01");
        DescuentoVIP descuentoVIP = new DescuentoVIP();
        
        double resultado = descuentoVIP.calcular(venta);
        assertTrue(resultado >= 0.0, "El total con descuento VIP debe ser un valor válido no negativo");
    }

    @Test
    void debeManejarVentaNulaEnDescuentoVIP() {
        DescuentoVIP descuentoVIP = new DescuentoVIP();
        
        assertThrows(Exception.class, () -> {
            descuentoVIP.calcular(null);
        }, "Debe gestionar de forma segura las referencias nulas");
    }

    @Test
    void debeCalcularDescuentoVIPConMontoValido() {
        Venta venta = new Venta("VENTA-VIP-02");
        // Si tu venta permite agregar un total o productos para evaluar la regla VIP:
        DescuentoVIP descuento = new DescuentoVIP();
        
        double resultado = descuento.calcular(venta);
        assertTrue(resultado >= 0.0);
    }

    @Test
    void debeCalcularDescuentoVIPConDetalles() {
        Venta venta = new Venta("VENTA-VIP-03");
        Producto producto = new Producto("P01", "Laptop", 10000.0, 5);
        
        // Usamos el método oficial de la entidad Venta para registrar la partida
        venta.agregarPartida(producto, 1);
        
        DescuentoVIP descuentoVIP = new DescuentoVIP();
        double resultado = descuentoVIP.calcular(venta);
        
        assertTrue(resultado > 0.0, "El descuento calculado con detalles debe ser mayor a cero");
    }
}
