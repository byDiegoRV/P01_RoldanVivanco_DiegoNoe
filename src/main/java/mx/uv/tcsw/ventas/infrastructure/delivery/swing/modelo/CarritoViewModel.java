package mx.uv.tcsw.ventas.infrastructure.delivery.swing.modelo;

public class CarritoViewModel {
    private String nombreProducto;
    private int cantidad;
    private double subtotal;

    public CarritoViewModel(String nombreProducto, int cantidad, double subtotal) {
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }
}