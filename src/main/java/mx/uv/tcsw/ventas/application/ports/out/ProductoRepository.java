package mx.uv.tcsw.ventas.application.ports.out;

import mx.uv.tcsw.ventas.domain.Producto;
import java.util.Optional;

public interface ProductoRepository {
    Optional<Producto> buscarPorCodigo(String codigo);
    void guardar(Producto producto);
}
