package mx.uv.tcsw.ventas.infrastructure.delivery.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.uv.tcsw.ventas.application.ports.in.ConsultarProductosUseCase;
import mx.uv.tcsw.ventas.infrastructure.delivery.rest.dto.ProductoRespuesta;

/** Adaptador de entrada HTTP para consultar el catálogo. Solo conoce el caso de uso. */
@RestController
@RequestMapping("/api/productos")
public class ProductosRestController {

    private final ConsultarProductosUseCase consultarProductos;

    public ProductosRestController(ConsultarProductosUseCase consultarProductos) {
        this.consultarProductos = consultarProductos;
    }

    @GetMapping
    public List<ProductoRespuesta> listar() {
        return consultarProductos.listar().stream()
                .map(ProductoRespuesta::desde)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProductoRespuesta obtener(@PathVariable int id) {
        return ProductoRespuesta.desde(consultarProductos.obtener(id));
    }
}
