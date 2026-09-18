package mx.uv.tcsw.ventas.application.service;

import mx.uv.tcsw.ventas.application.ports.in.RegistrarVentaUseCase;
import mx.uv.tcsw.ventas.application.ports.out.VentaRepository;
import mx.uv.tcsw.ventas.domain.Venta;

public class RegistrarVentaService implements RegistrarVentaUseCase {

    private final VentaRepository ventaRepository;

    // Inyección de dependencias a través del constructor (Puerto de Salida)
    public RegistrarVentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

   @Override
    public void ejecutar(Venta venta) {
        // Lógica de validación del negocio utilizando el método correcto del dominio
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un producto.");
        }
        
        // Delegamos la persistencia al puerto de salida
        ventaRepository.guardar(venta);
    }
}