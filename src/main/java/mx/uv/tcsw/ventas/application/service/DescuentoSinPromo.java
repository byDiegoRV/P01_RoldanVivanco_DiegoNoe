package mx.uv.tcsw.ventas.application.service;

import mx.uv.tcsw.ventas.domain.Venta;

public class DescuentoSinPromo implements CalculadorDescuentoStrategy {
    @Override
    public double calcular(Venta venta) {
        return 0.0;
    }
}

