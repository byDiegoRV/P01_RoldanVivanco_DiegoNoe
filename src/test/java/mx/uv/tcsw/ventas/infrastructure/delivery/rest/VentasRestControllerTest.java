package mx.uv.tcsw.ventas.infrastructure.delivery.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import mx.uv.tcsw.ventas.application.exception.ProductoNoEncontradoException;
import mx.uv.tcsw.ventas.application.ports.in.VenderProductosUseCase;
import mx.uv.tcsw.ventas.domain.ExistenciaInsuficienteException;
import mx.uv.tcsw.ventas.domain.Producto;
import mx.uv.tcsw.ventas.domain.Venta;

@WebMvcTest(VentasRestController.class)
@ActiveProfiles("test")
class VentasRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private VenderProductosUseCase venderProductos;

    @Test
    @DisplayName("POST /api/ventas con datos válidos devuelve 201 con folio, total y partidas")
    void ventaValidaDevuelve201() throws Exception {
        Venta venta = new Venta("V-20261102101530123");
        venta.agregarPartida(new Producto(1, "P-001", "Laptop X", 15000.0, 10), 2);
        when(venderProductos.ejecutar(any())).thenReturn(venta);

        mvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"partidas\":[{\"productoId\":1,\"cantidad\":2}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.folio").value("V-20261102101530123"))
                .andExpect(jsonPath("$.total").value(30000.0))
                .andExpect(jsonPath("$.partidas[0].productoId").value(1))
                .andExpect(jsonPath("$.partidas[0].nombre").value("Laptop X"))
                .andExpect(jsonPath("$.partidas[0].cantidad").value(2))
                .andExpect(jsonPath("$.partidas[0].subtotal").value(30000.0));
    }

    @Test
    @DisplayName("Una cantidad menor a 1 devuelve 400 y no llega al caso de uso")
    void cantidadInvalidaDevuelve400() throws Exception {
        mvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"partidas\":[{\"productoId\":1,\"cantidad\":0}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDACION_FALLIDA"))
                .andExpect(jsonPath("$.message").value("La petición contiene datos inválidos."))
                .andExpect(jsonPath("$.details[0]").value("partidas[0].cantidad: debe ser mayor o igual a 1"))
                .andExpect(jsonPath("$.path").value("/api/ventas"));

        verify(venderProductos, never()).ejecutar(any());
    }

    @Test
    @DisplayName("Una lista de partidas vacía devuelve 400")
    void partidasVaciasDevuelve400() throws Exception {
        mvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"partidas\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDACION_FALLIDA"))
                .andExpect(jsonPath("$.details[0]").value("partidas: debe incluir al menos una partida"));

        verify(venderProductos, never()).ejecutar(any());
    }

    @Test
    @DisplayName("Un campo desconocido, como datos de tarjeta, devuelve 400")
    void campoDesconocidoDevuelve400() throws Exception {
        mvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tarjeta\":\"1234567890123456\",\"partidas\":[{\"productoId\":1,\"cantidad\":1}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("SOLICITUD_INVALIDA"))
                .andExpect(content().string(not(containsString("1234567890123456"))));

        verify(venderProductos, never()).ejecutar(any());
    }

    @Test
    @DisplayName("Un JSON mal formado devuelve 400")
    void jsonMalFormadoDevuelve400() throws Exception {
        mvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"partidas\": ["))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("SOLICITUD_INVALIDA"));
    }

    @Test
    @DisplayName("Un producto inexistente devuelve 404 con el formato de error")
    void productoInexistenteDevuelve404() throws Exception {
        when(venderProductos.ejecutar(any())).thenThrow(new ProductoNoEncontradoException(999));

        mvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"partidas\":[{\"productoId\":999,\"cantidad\":1}]}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("PRODUCTO_NO_ENCONTRADO"))
                .andExpect(jsonPath("$.message").value("No existe el producto con id 999."))
                .andExpect(jsonPath("$.path").value("/api/ventas"));
    }

    @Test
    @DisplayName("Sin existencia suficiente devuelve 409 con el formato de error")
    void existenciaInsuficienteDevuelve409() throws Exception {
        when(venderProductos.ejecutar(any()))
                .thenThrow(new ExistenciaInsuficienteException("Existencia insuficiente.", 3));

        mvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"partidas\":[{\"productoId\":3,\"cantidad\":1}]}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code").value("EXISTENCIA_INSUFICIENTE"))
                .andExpect(jsonPath("$.message").value("No hay existencia suficiente para el producto con id 3."));
    }

    @Test
    @DisplayName("Un error inesperado al registrar devuelve 500 genérico, sin exponer el detalle")
    void errorInesperadoDevuelve500() throws Exception {
        when(venderProductos.ejecutar(any())).thenThrow(new IllegalStateException("fallo con password=secreto"));

        mvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"partidas\":[{\"productoId\":1,\"cantidad\":1}]}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("ERROR_INTERNO"))
                .andExpect(jsonPath("$.message").value("Ocurrió un error interno. Intenta de nuevo más tarde."))
                .andExpect(content().string(not(containsString("secreto"))));
    }
}
