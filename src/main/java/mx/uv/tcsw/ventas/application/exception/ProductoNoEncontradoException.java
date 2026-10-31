package mx.uv.tcsw.ventas.application.exception;

/** No existe un producto con el identificador solicitado. */
public class ProductoNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int productoId;

    public ProductoNoEncontradoException(int productoId) {
        super("No existe el producto con id " + productoId + ".");
        this.productoId = productoId;
    }

    public int getProductoId() {
        return productoId;
    }
}
