package mx.uv.tcsw.ventas.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.application.exception.ProductoNoEncontradoException;
import mx.uv.tcsw.ventas.application.ports.out.ProductoRepository;
import mx.uv.tcsw.ventas.domain.Producto;

class ConsultarProductosServiceTest {

    private ProductoRepository repositorio;
    private ConsultarProductosService servicio;

    @BeforeEach
    void setUp() {
        repositorio = mock(ProductoRepository.class);
        servicio = new ConsultarProductosService(repositorio);
    }

    @Test
    @DisplayName("Lista los productos del repositorio")
    void listaLosProductosDelRepositorio() {
        List<Producto> productos = Arrays.asList(
                new Producto(1, "P-001", "Laptop X", 15000.0, 10),
                new Producto(2, "P-002", "Teclado", 1200.0, 25));
        when(repositorio.listarTodos()).thenReturn(productos);

        assertEquals(productos, servicio.listar());
    }

    @Test
    @DisplayName("Obtiene un producto existente por id")
    void obtieneProductoExistente() {
        Producto producto = new Producto(1, "P-001", "Laptop X", 15000.0, 10);
        when(repositorio.buscarPorId(1)).thenReturn(Optional.of(producto));

        assertSame(producto, servicio.obtener(1));
    }

    @Test
    @DisplayName("Un producto inexistente lanza ProductoNoEncontradoException con su id")
    void productoInexistenteLanzaExcepcion() {
        when(repositorio.buscarPorId(999)).thenReturn(Optional.empty());

        ProductoNoEncontradoException ex = assertThrows(ProductoNoEncontradoException.class,
                () -> servicio.obtener(999));

        assertEquals(999, ex.getProductoId());
    }
}
