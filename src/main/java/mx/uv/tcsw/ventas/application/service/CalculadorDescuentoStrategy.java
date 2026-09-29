package mx.uv.tcsw.ventas.application.service;

import mx.uv.tcsw.ventas.domain.Venta;

public interface CalculadorDescuentoStrategy {
    double calcular(Venta venta);
}