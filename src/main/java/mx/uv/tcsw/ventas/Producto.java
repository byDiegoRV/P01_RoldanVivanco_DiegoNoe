package mx.uv.tcsw.ventas;

import java.util.Objects;

/**
 * Entidad Producto del prototipo de ventas.
 *
 * <p>Representa un articulo comerciable con codigo, nombre, precio y
 * existencia. La clase protege sus invariantes mediante encapsulamiento:
 * los atributos son privados e inmutables desde fuera del objeto salvo por
 * metodos de negocio explicitos y validados.</p>
 *
 */
public final class Producto {

    private final String codigo;
    private final String nombre;
    private double precio;
    private int existencia;

    /**
     * Crea un Producto valido.
     *
     * @param codigo     identificador unico del producto, no nulo ni en blanco
     * @param nombre     nombre descriptivo del producto, no nulo ni en blanco
     * @param precio     precio unitario, debe ser mayor o igual a cero
     * @param existencia cantidad disponible en inventario, debe ser mayor o igual a cero
     * @throws IllegalArgumentException si algun dato viola el invariante de la clase
     */
    public Producto(String codigo, String nombre, double precio, int existencia) {
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

    /**
     * Actualiza el precio del producto, preservando el invariante de no
     * negatividad.
     *
     * @param nuevoPrecio nuevo precio unitario, debe ser mayor o igual a cero
     * @throws IllegalArgumentException si el nuevo precio es negativo o invalido
     */
    public void actualizarPrecio(double nuevoPrecio) {
        this.precio = validarPrecio(nuevoPrecio);
    }

    /**
     * Incrementa la existencia del producto (por ejemplo, tras una compra a
     * proveedor).
     *
     * @param cantidad cantidad a agregar, debe ser mayor a cero
     * @throws IllegalArgumentException si la cantidad no es positiva
     */
    public void agregarExistencia(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a agregar debe ser mayor a cero.");
        }
        this.existencia += cantidad;
    }

    /**
     * Descuenta existencia del producto (por ejemplo, tras una venta).
     *
     * <p>Contrato:</p>
     * <ul>
     *   <li>Entrada: entero positivo.</li>
     *   <li>Precondicion: {@code cantidad} no mayor que la existencia actual.</li>
     *   <li>Resultado: existencia anterior menos {@code cantidad}.</li>
     *   <li>Fallo: rechazo sin modificar el estado del objeto.</li>
     * </ul>
     *
     * <p>Se distingue el tipo de excepcion segun la causa del rechazo: un
     * argumento invalido en si mismo (cantidad no positiva) se senala con
     * {@link IllegalArgumentException}; una operacion imposible por el
     * estado actual del objeto (existencia insuficiente) se senala con
     * {@link IllegalStateException}. En ambos casos toda precondicion se
     * valida antes de modificar {@code existencia}, de modo que un rechazo
     * nunca deja el objeto parcialmente modificado.</p>
     *
     * @param cantidad cantidad a descontar, debe ser mayor a cero y no exceder la existencia actual
     * @throws IllegalArgumentException si la cantidad no es positiva
     * @throws IllegalStateException    si la cantidad excede la existencia disponible
     */
    public void descontar(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva.");
        }
        if (cantidad > this.existencia) {
            throw new IllegalStateException("Existencia insuficiente.");
        }
        this.existencia -= cantidad;
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
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", existencia=" + existencia +
                '}';
    }
}
