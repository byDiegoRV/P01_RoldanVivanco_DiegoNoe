package mx.uv.tcsw.ventas.application.ports.in;

/** Partida solicitada para una venta: producto y cantidad. */
public final class ItemVenta {

    private final int productoId;
    private final int cantidad;

    public ItemVenta(int productoId, int cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    public int getProductoId() {
        return productoId;
    }

    public int getCantidad() {
        return cantidad;
    }
}
