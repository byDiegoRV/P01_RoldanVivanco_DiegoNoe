package mx.uv.tcsw.ventas.infrastructure.delivery.swing.modelo;

public class ProductoViewModel {
    private String codigo;
    private String nombre;
    private double precio;

    public ProductoViewModel(String codigo, String nombre, double precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }
}