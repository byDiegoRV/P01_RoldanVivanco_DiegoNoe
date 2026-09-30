package mx.uv.tcsw.ventas.application.service;

import mx.uv.tcsw.ventas.domain.DetalleVenta;
import mx.uv.tcsw.ventas.domain.Venta;

public class DescuentoVIP implements CalculadorDescuentoStrategy {
    @Override
    public double calcular(Venta venta) {
        double subtotal = 0.0;
        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                // Multiplico la cantidad por el precio obtenido desde el producto
                subtotal += detalle.getCantidad() * detalle.getProducto().getPrecio();
            }
        }
        // Aplico el 15% de descuento sobre el subtotal calculado
        return subtotal * 0.15;
    }
}