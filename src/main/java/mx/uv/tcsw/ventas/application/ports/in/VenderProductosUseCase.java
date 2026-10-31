package mx.uv.tcsw.ventas.application.ports.in;

import java.util.List;

import mx.uv.tcsw.ventas.domain.Venta;

/** Caso de uso: armar y registrar una venta a partir de productos y cantidades. */
public interface VenderProductosUseCase {

    /**
     * @throws IllegalArgumentException si no hay partidas o una cantidad no es válida
     * @throws mx.uv.tcsw.ventas.application.exception.ProductoNoEncontradoException si un producto no existe
     * @throws mx.uv.tcsw.ventas.domain.ExistenciaInsuficienteException si no alcanza la existencia
     */
    Venta ejecutar(List<ItemVenta> items);
}
