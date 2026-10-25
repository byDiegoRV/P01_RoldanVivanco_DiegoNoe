package mx.uv.tcsw.ventas.infrastructure.delivery.swing.modelo;

import mx.uv.tcsw.ventas.domain.Producto;

public class CarritoViewModel {
    private Producto producto;
    private int cantidad;
    private boolean seleccionado; 

    public CarritoViewModel(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.seleccionado = true;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = Math.max(1, cantidad); 
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }
}