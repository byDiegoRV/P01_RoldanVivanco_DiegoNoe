package mx.uv.tcsw.ventas.domain;

import java.util.Objects;

/**
 * Entidad Producto del prototipo de ventas.
 *
 * <p>Representa un articulo comerciable con codigo, nombre, precio y
 * existencia. La clase protege sus invariantes mediante encapsulamiento:
 * los atributos son privados e inmutables desde fuera del objeto salvo por
 * metodos de negocio explicitos y validados.</p>
 */
public final class Producto {

    private Integer id; // Identificador autoincrementable generado por la base de datos
    private final String codigo;
    private final String nombre;
    private double precio;
    private int existencia;

    /**
     * Crea un Producto nuevo (sin ID asignado, ideal para inserciones).
     *
     * @param codigo     identificador unico del producto, no nulo ni en blanco
     * @param nombre     nombre descriptivo del producto, no nulo ni en blanco
     * @param precio     precio unitario, debe ser mayor o igual a cero
     * @param existencia cantidad disponible en inventario, debe ser mayor o igual a cero
     */
    public Producto(String codigo, String nombre, double precio, int existencia) {
        this.id = null;
        this.codigo = validarTexto(codigo, "codigo");
        this.nombre = validarTexto(nombre, "nombre");
        this.precio = validarPrecio(precio);
        this.existencia = validarExistencia(existencia);
    }

    /**
     * Crea un Producto existente recuperado de la base de datos (con ID asignado).
     */
    public Producto(Integer id, String codigo, String nombre, double precio, int existencia) {
        this.id = id;
        this.codigo = validarTexto(codigo, "codigo");
        this.nombre = validarTexto(nombre, "nombre");
        this.precio = validarPrecio(precio);
        this.existencia = validarExistencia(existencia);
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "El campo '" + campo + "' no puede ser nulo ni estar en blanco.");
        }
        return valor;
    }

    private static double validarPrecio(double precio) {
        if (Double.isNaN(precio) || precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        return precio;
    }

    private static int validarExistencia(int existencia) {
        if (existencia < 0) {
            throw new IllegalArgumentException("La existencia no puede ser negativa.");
        }
        return existencia;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public void actualizarPrecio(double nuevoPrecio) {
        this.precio = validarPrecio(nuevoPrecio);
    }

    public void agregarExistencia(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a agregar debe ser mayor a cero.");
        }
        this.existencia += cantidad;
    }

    public void descontar(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva.");
        }
        if (cantidad > this.existencia) {
            throw new IllegalStateException("Existencia insuficiente.");
        }
        this.existencia -= cantidad;
    }

    public double precioConDescuento(double porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100.");
        }
        double descuento = precio * (porcentaje / 100);
        return precio - descuento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Producto)) {
            return false;
        }
        Producto producto = (Producto) o;
        return codigo.equals(producto.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", existencia=" + existencia +
                '}';
    }

    public boolean estaAgotado() {
        return this.existencia <= 0;
    }
}