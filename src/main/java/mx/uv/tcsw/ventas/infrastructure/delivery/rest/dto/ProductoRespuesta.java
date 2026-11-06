package mx.uv.tcsw.ventas.infrastructure.delivery.rest.dto;

import mx.uv.tcsw.ventas.domain.Producto;

/** Representación JSON de un producto. */
public class ProductoRespuesta {

    private final Integer id;
    private final String codigo;
    private final String nombre;
    private final double precio;
    private final int existencia;

    public ProductoRespuesta(Integer id, String codigo, String nombre, double precio, int existencia) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.existencia = existencia;
    }

    public static ProductoRespuesta desde(Producto producto) {
        return new ProductoRespuesta(producto.getId(), producto.getCodigo(), producto.getNombre(),
                producto.getPrecio(), producto.getExistencia());
    }

    public Integer getId() {
        return id;
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

    public int getExistencia() {
        return existencia;
    }
}
