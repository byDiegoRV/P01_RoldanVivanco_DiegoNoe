package mx.uv.tcsw.ventas.application.ports.in;

import mx.uv.tcsw.ventas.domain.Venta;

public interface RegistrarVentaUseCase {
    void ejecutar(Venta venta);
}