package mx.uv.tcsw.ventas.application.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import mx.uv.tcsw.ventas.application.exception.ProductoNoEncontradoException;
import mx.uv.tcsw.ventas.application.ports.in.ItemVenta;
import mx.uv.tcsw.ventas.application.ports.in.RegistrarVentaUseCase;
import mx.uv.tcsw.ventas.application.ports.in.VenderProductosUseCase;
import mx.uv.tcsw.ventas.application.ports.out.ProductoRepository;
import mx.uv.tcsw.ventas.domain.ExistenciaInsuficienteException;
import mx.uv.tcsw.ventas.domain.Producto;
import mx.uv.tcsw.ventas.domain.Venta;

/**
 * Arma la venta con los productos actuales del repositorio (el dominio valida la existencia)
 * y delega el registro transaccional en {@link RegistrarVentaUseCase}.
 */
public class VenderProductosService implements VenderProductosUseCase {

    private static final DateTimeFormatter FORMATO_FOLIO = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final ProductoRepository productoRepository;
    private final RegistrarVentaUseCase registrarVenta;
    private final Clock reloj;

    public VenderProductosService(ProductoRepository productoRepository, RegistrarVentaUseCase registrarVenta) {
        this(productoRepository, registrarVenta, Clock.systemDefaultZone());
    }

    public VenderProductosService(ProductoRepository productoRepository, RegistrarVentaUseCase registrarVenta,
            Clock reloj) {
        this.productoRepository = productoRepository;
        this.registrarVenta = registrarVenta;
        this.reloj = reloj;
    }

    @Override
    public Venta ejecutar(List<ItemVenta> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos una partida.");
        }

        Venta venta = new Venta("V-" + LocalDateTime.now(reloj).format(FORMATO_FOLIO));
        for (ItemVenta item : items) {
            Producto producto = productoRepository.buscarPorId(item.getProductoId())
                    .orElseThrow(() -> new ProductoNoEncontradoException(item.getProductoId()));
            venta.agregarPartida(producto, item.getCantidad());
        }

        try {
            registrarVenta.ejecutar(venta);
        } catch (IllegalStateException ex) {
            throw traducir(ex);
        }
        return venta;
    }

    /** Si la causa raíz es falta de existencia (detectada por la base), la expone sin el envoltorio. */
    private static RuntimeException traducir(IllegalStateException ex) {
        Throwable causa = ex;
        while (causa != null) {
            if (causa instanceof ExistenciaInsuficienteException) {
                return (ExistenciaInsuficienteException) causa;
            }
            causa = causa.getCause();
        }
        return ex;
    }
}
