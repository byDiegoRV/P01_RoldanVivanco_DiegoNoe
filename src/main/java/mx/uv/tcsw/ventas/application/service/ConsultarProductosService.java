package mx.uv.tcsw.ventas.application.service;

import java.util.List;

import mx.uv.tcsw.ventas.application.exception.ProductoNoEncontradoException;
import mx.uv.tcsw.ventas.application.ports.in.ConsultarProductosUseCase;
import mx.uv.tcsw.ventas.application.ports.out.ProductoRepository;
import mx.uv.tcsw.ventas.domain.Producto;

public class ConsultarProductosService implements ConsultarProductosUseCase {

    private final ProductoRepository productoRepository;

    public ConsultarProductosService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> listar() {
        return productoRepository.listarTodos();
    }

    @Override
    public Producto obtener(int id) {
        return productoRepository.buscarPorId(id).orElseThrow(() -> new ProductoNoEncontradoException(id));
    }
}
