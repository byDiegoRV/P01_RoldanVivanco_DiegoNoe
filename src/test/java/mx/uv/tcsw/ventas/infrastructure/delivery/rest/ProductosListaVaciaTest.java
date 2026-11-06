package mx.uv.tcsw.ventas.infrastructure.delivery.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import mx.uv.tcsw.ventas.application.ports.in.ConsultarProductosUseCase;
import mx.uv.tcsw.ventas.application.ports.in.VenderProductosUseCase;

@WebMvcTest({ProductosRestController.class, VentasRestController.class})
@ActiveProfiles("test")
class ProductosListaVaciaTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ConsultarProductosUseCase consultarProductos;

    @MockBean
    private VenderProductosUseCase venderProductos;

    @Test
    @DisplayName("Sin productos, GET /api/productos devuelve 200 y una lista vacía")
    void sinProductosDevuelveListaVacia() throws Exception {
        when(consultarProductos.listar()).thenReturn(Collections.emptyList());

        mvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Una venta sin cuerpo devuelve 400 con el formato de error")
    void ventaSinCuerpoDevuelve400() throws Exception {
        mvc.perform(post("/api/ventas").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("SOLICITUD_INVALIDA"));
    }
}
