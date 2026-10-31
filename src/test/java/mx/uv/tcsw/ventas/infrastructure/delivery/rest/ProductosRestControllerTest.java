package mx.uv.tcsw.ventas.infrastructure.delivery.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import mx.uv.tcsw.ventas.application.exception.ProductoNoEncontradoException;
import mx.uv.tcsw.ventas.application.ports.in.ConsultarProductosUseCase;
import mx.uv.tcsw.ventas.domain.Producto;

@WebMvcTest(ProductosRestController.class)
@ActiveProfiles("test")
class ProductosRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ConsultarProductosUseCase consultarProductos;

    @Test
    @DisplayName("GET /api/productos devuelve 200 con la lista de productos")
    void listaProductos() throws Exception {
        when(consultarProductos.listar()).thenReturn(Arrays.asList(
                new Producto(1, "P-001", "Laptop X", 15000.0, 10),
                new Producto(3, "P-003", "Mouse Inalámbrico", 450.0, 0)));

        mvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].codigo").value("P-001"))
                .andExpect(jsonPath("$[0].nombre").value("Laptop X"))
                .andExpect(jsonPath("$[0].precio").value(15000.0))
                .andExpect(jsonPath("$[1].existencia").value(0));
    }

    @Test
    @DisplayName("GET /api/productos devuelve 200 y lista vacía si no hay productos")
    void listaVacia() throws Exception {
        when(consultarProductos.listar()).thenReturn(Collections.emptyList());

        mvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/productos/{id} devuelve 200 con el producto")
    void obtieneProducto() throws Exception {
        when(consultarProductos.obtener(1)).thenReturn(new Producto(1, "P-001", "Laptop X", 15000.0, 10));

        mvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Laptop X"))
                .andExpect(jsonPath("$.existencia").value(10));
    }

    @Test
    @DisplayName("GET /api/productos/{id} de un producto inexistente devuelve 404 con el formato de error")
    void productoInexistenteDevuelve404() throws Exception {
        when(consultarProductos.obtener(999)).thenThrow(new ProductoNoEncontradoException(999));

        mvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("PRODUCTO_NO_ENCONTRADO"))
                .andExpect(jsonPath("$.message").value("No existe el producto con id 999."))
                .andExpect(jsonPath("$.path").value("/api/productos/999"))
                .andExpect(jsonPath("$.details.length()").value(0))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GET /api/productos/{id} con un id no numérico devuelve 400")
    void idNoNumericoDevuelve400() throws Exception {
        mvc.perform(get("/api/productos/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("SOLICITUD_INVALIDA"))
                .andExpect(jsonPath("$.message").value("El parámetro 'id' debe ser un número entero."))
                .andExpect(jsonPath("$.path").value("/api/productos/abc"));
    }

    @Test
    @DisplayName("Un error inesperado devuelve 500 con mensaje genérico, sin exponer el detalle")
    void errorInesperadoDevuelve500SinDetalles() throws Exception {
        when(consultarProductos.listar()).thenThrow(new IllegalStateException("fallo con password=secreto"));

        mvc.perform(get("/api/productos"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("ERROR_INTERNO"))
                .andExpect(jsonPath("$.message").value("Ocurrió un error interno. Intenta de nuevo más tarde."))
                .andExpect(content().string(not(containsString("secreto"))));
    }
}
