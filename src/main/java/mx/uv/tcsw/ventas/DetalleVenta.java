package mx.uv.tcsw.ventas;

import java.util.Objects;

/**
 * Linea de una venta (objeto de valor).
 *
 * <p>Representa la venta de una cantidad de un {@link Producto} a un precio
 * unitario congelado en el momento en el que se agrego a la venta. El
 * precio no se vuelve a consultar despues: si el precio del producto en el
 * catalogo cambia mas tarde, este detalle conserva el precio con el que
 * realmente se vendio.</p>
 *
 * <p>{@code DetalleVenta} es un objeto de valor: no tiene un identificador
 * propio. Dos instancias se consideran iguales si representan la misma
 * combinacion de producto, cantidad y precio unitario, sin importar si son
 * el mismo objeto en memoria.</p>
 */
public final class DetalleVenta {

    private final Producto producto;
    private final int cantidad;
    private final double precioUnitario;

    /**
     * Crea una linea de venta valida.
     *
     * @param producto       producto vendido, no nulo
     * @param cantidad       cantidad vendida, debe ser mayor a cero
     * @param precioUnitario precio unitario congelado al momento de la venta, debe ser mayor o igual a cero
     * @throws IllegalArgumentException si algun dato viola el invariante de la clase
     */
    public DetalleVenta(Producto producto, int cantidad, double precioUnitario) {
        this.producto = validarProducto(producto);
        this.cantidad = validarCantidad(cantidad);
        this.precioUnitario = validarPrecio(precioUnitario);
    }

    private static Producto validarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        return producto;
    }

    private static int validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        return cantidad;
    }

    private static double validarPrecio(double precioUnitario) {
        if (Double.isNaN(precioUnitario) || precioUnitario < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
        }
        return precioUnitario;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Calcula el subtotal de esta linea: cantidad por precio unitario
     * congelado.
     *
     * @return el subtotal de la linea
     */
    public double subtotal() {
        return cantidad * precioUnitario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetalleVenta)) {
            return false;
        }
        DetalleVenta that = (DetalleVenta) o;
        return cantidad == that.cantidad
                && Double.compare(that.precioUnitario, precioUnitario) == 0
                && producto.equals(that.producto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(producto, cantidad, precioUnitario);
    }

    @Override
    public String toString() {
        return "DetalleVenta{" +
                "producto=" + producto.getCodigo() +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal() +
                '}';
    }
}