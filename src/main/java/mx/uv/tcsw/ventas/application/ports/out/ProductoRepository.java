package mx.uv.tcsw.ventas.application.ports.out;

import java.util.Optional;

import mx.uv.tcsw.ventas.domain.Producto;

public interface ProductoRepository {
    void guardar(Producto producto);
    Optional<Producto> buscarPorId(Integer id);
    
}