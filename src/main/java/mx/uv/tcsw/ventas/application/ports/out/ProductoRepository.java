package mx.uv.tcsw.ventas.application.ports.out;

import java.util.List;
import java.util.Optional;

import mx.uv.tcsw.ventas.domain.Producto;

public interface ProductoRepository {
    void guardar(Producto producto);
    Optional<Producto> buscarPorId(Integer id);
    Optional<Producto> buscarPorCodigo(String codigo);
    List<Producto> listarTodos();
    void actualizar(Producto producto);
    void eliminar(Integer id);
}