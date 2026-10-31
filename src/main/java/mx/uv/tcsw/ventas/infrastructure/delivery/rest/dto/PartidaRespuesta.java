package mx.uv.tcsw.ventas.infrastructure.delivery.rest.dto;

import mx.uv.tcsw.ventas.domain.DetalleVenta;

public class PartidaRespuesta {

    private final Integer productoId;
    private final String nombre;
    private final int cantidad;
    private final double precioUnitario;
    private final double subtotal;

    public PartidaRespuesta(Integer productoId, String nombre, int cantidad, double precioUnitario,
            double subtotal) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public static PartidaRespuesta desde(DetalleVenta detalle) {
        return new PartidaRespuesta(detalle.getProducto().getId(), detalle.getProducto().getNombre(),
                detalle.getCantidad(), detalle.getPrecioUnitario(), detalle.subtotal());
    }

    public Integer getProductoId() {
        return productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }
}
