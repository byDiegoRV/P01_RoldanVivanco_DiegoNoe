package mx.uv.tcsw.ventas.application.ports.out;

import mx.uv.tcsw.ventas.domain.Venta;

public interface VentaRepository {
    void guardar(Venta venta);
}
