package mx.uv.tcsw.ventas.infrastructure.delivery.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import mx.uv.tcsw.ventas.application.ports.in.VenderProductosUseCase;
import mx.uv.tcsw.ventas.domain.ExistenciaInsuficienteException;

/** Errores estándar de Spring MVC y variantes del manejador que no cubren las pruebas de cada controlador. */
@WebMvcTest(VentasRestController.class)
@ActiveProfiles("test")
class ManejadorErroresTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private VenderProductosUseCase venderProductos;

    @Test
    @DisplayName("Una ruta inexistente devuelve 404 con el formato de error")
    void rutaInexistenteDevuelve404() throws Exception {
        mvc.perform(get("/api/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RECURSO_NO_ENCONTRADO"))
                .andExpect(jsonPath("$.message").value("El recurso solicitado no existe."))
                .andExpect(jsonPath("$.path").value("/api/no-existe"));
    }

    @Test
    @DisplayName("Un método no permitido devuelve 405 con el formato de error")
    void metodoNoPermitidoDevuelve405() throws Exception {
        mvc.perform(get("/api/ventas"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.code").value("METODO_NO_PERMITIDO"))
                .andExpect(jsonPath("$.path").value("/api/ventas"));
    }

    @Test
    @DisplayName("Un tipo de contenido no soportado devuelve 415 con el formato de error")
    void tipoNoSoportadoDevuelve415() throws Exception {
        mvc.perform(post("/api/ventas")
                .contentType(MediaType.TEXT_PLAIN)
                .content("partidas"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.code").value("TIPO_NO_SOPORTADO"));
    }

    @Test
    @DisplayName("Sin existencia y sin id de producto devuelve 409 con mensaje genérico")
    void existenciaInsuficienteSinIdDevuelve409() throws Exception {
        when(venderProductos.ejecutar(any()))
                .thenThrow(new ExistenciaInsuficienteException("Existencia insuficiente.", null));

        mvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"partidas\":[{\"productoId\":1,\"cantidad\":1}]}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EXISTENCIA_INSUFICIENTE"))
                .andExpect(jsonPath("$.message").value("No hay existencia suficiente para completar la venta."));
    }
}
