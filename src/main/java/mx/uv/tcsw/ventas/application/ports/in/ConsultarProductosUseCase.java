package mx.uv.tcsw.ventas.application.ports.in;

import java.util.List;

import mx.uv.tcsw.ventas.domain.Producto;

/** Caso de uso: consultar el catálogo de productos. */
public interface ConsultarProductosUseCase {

    List<Producto> listar();

    /**
     * @throws mx.uv.tcsw.ventas.application.exception.ProductoNoEncontradoException si no existe el producto
     */
    Producto obtener(int id);
}
