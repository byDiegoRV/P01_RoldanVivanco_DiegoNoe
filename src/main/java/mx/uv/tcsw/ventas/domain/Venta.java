package mx.uv.tcsw.ventas.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Venta del prototipo de ventas (entidad con identidad propia).
 *
 * <p>Una {@code Venta} compone una lista de {@link DetalleVenta}: si la
 * venta deja de existir, sus detalles no tienen sentido por separado. A
 * diferencia de {@code DetalleVenta} (objeto de valor), {@code Venta} tiene
 * identidad: dos ventas se consideran la misma si comparten el mismo
 * {@code folio}, sin importar si sus detalles llegaran a diferir.</p>
 */
public final class Venta {

    private final String folio;
    private final List<DetalleVenta> detalles = new ArrayList<>();

    /**
     * Crea una venta vacia identificada por su folio.
     *
     * @param folio identificador unico de la venta, no nulo ni en blanco
     * @throws IllegalArgumentException si el folio es nulo o esta en blanco
     */
    public Venta(String folio) {
        this.folio = validarFolio(folio);
    }

    private static String validarFolio(String folio) {
        if (folio == null || folio.isBlank()) {
            throw new IllegalArgumentException("El folio no puede ser nulo ni estar en blanco.");
        }
        return folio;
    }

    public String getFolio() {
        return folio;
    }

    /**
     * Devuelve una vista de solo lectura de los detalles de la venta, para
     * no romper el encapsulamiento permitiendo que quien la llame modifique
     * la lista interna directamente.
     *
     * @return lista no modificable de los detalles actuales de la venta
     */
    public List<DetalleVenta> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    /**
     * Agrega una partida a la venta: descuenta la existencia del producto y,
     * si el descuento tiene exito, registra una linea con el precio
     * congelado en este momento.
     *
     * <p>Toda precondicion (cantidad valida, existencia suficiente) se
     * valida mediante {@link Producto#descontar(int)} antes de modificar la
     * lista de detalles de la venta, de modo que un rechazo nunca deja la
     * venta parcialmente modificada.</p>
     *
     * @param producto producto a vender, no nulo
     * @param cantidad cantidad a vender, debe ser mayor a cero
     * @throws IllegalArgumentException si el producto es nulo o la cantidad no es positiva
     * @throws IllegalStateException    si la cantidad excede la existencia disponible del producto
     */
    public void agregarPartida(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        double precioAlMomentoDeVender = producto.getPrecio();
        producto.descontar(cantidad);

        detalles.add(new DetalleVenta(producto, cantidad, precioAlMomentoDeVender));
    }

    /**
     * Elimina una partida de la venta si se encuentra presente.
     *
     * @param detalle la partida a eliminar, puede ser nula
     * @return {@code true} si la partida estaba presente y fue eliminada; {@code false} de lo contrario
     */
    public boolean eliminarPartida(DetalleVenta detalle) {
        if (detalle == null) {
            return false;
        }
        return this.detalles.remove(detalle);
    }

    /**
   * Calcula el total de la venta como la suma de los subtotales
   * de cada uno de sus detalles.
   */
  
    public double calcularTotal() {
        double total = 0.0;
        for (DetalleVenta detalle : detalles) {
            total += detalle.subtotal();
        }
        return total;
    }

    /**
     * Devuelve el total formateado a dos decimales asegurando el punto decimal.
     *
     * @return representación en cadena del total con formato numérico estándar
     */
    public String totalFormateado() {
        return String.format(Locale.US, "%.2f", calcularTotal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Venta)) {
            return false;
        }
        Venta venta = (Venta) o;
        return folio.equals(venta.folio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(folio);
    }

    @Override
    public String toString() {
        return "Venta{" +
                "folio='" + folio + '\'' +
                ", detalles=" + detalles.size() +
                ", total=" + calcularTotal() +
                '}';
    }
}

