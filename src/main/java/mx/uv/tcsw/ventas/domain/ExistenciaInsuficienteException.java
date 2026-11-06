package mx.uv.tcsw.ventas.domain;

/**
 * La cantidad solicitada excede la existencia disponible de un producto.
 * Es una {@link IllegalStateException}, por lo que el código existente que la captura sigue funcionando.
 */
public class ExistenciaInsuficienteException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    private final transient Integer productoId;

    public ExistenciaInsuficienteException(String mensaje, Integer productoId) {
        super(mensaje);
        this.productoId = productoId;
    }

    /** Identificador del producto afectado, o {@code null} si no se conoce. */
    public Integer getProductoId() {
        return productoId;
    }
}
