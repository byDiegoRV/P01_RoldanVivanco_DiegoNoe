package mx.uv.tcsw.ventas.infrastructure.delivery.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import mx.uv.tcsw.ventas.application.ports.in.ItemVenta;
import mx.uv.tcsw.ventas.application.ports.in.VenderProductosUseCase;
import mx.uv.tcsw.ventas.infrastructure.delivery.rest.dto.VentaPeticion;
import mx.uv.tcsw.ventas.infrastructure.delivery.rest.dto.VentaRespuesta;

/** Adaptador de entrada HTTP para registrar ventas. Solo conoce el caso de uso. */
@RestController
@RequestMapping("/api/ventas")
public class VentasRestController {

    private final VenderProductosUseCase venderProductos;

    public VentasRestController(VenderProductosUseCase venderProductos) {
        this.venderProductos = venderProductos;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaRespuesta registrar(@Valid @RequestBody VentaPeticion peticion) {
        List<ItemVenta> items = peticion.getPartidas().stream()
                .map(p -> new ItemVenta(p.getProductoId(), p.getCantidad()))
                .collect(Collectors.toList());
        return VentaRespuesta.desde(venderProductos.ejecutar(items));
    }
}
