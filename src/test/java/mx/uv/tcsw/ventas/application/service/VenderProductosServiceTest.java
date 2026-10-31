package mx.uv.tcsw.ventas.application.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import mx.uv.tcsw.ventas.application.exception.ProductoNoEncontradoException;
import mx.uv.tcsw.ventas.application.ports.in.ItemVenta;
import mx.uv.tcsw.ventas.application.ports.in.RegistrarVentaUseCase;
import mx.uv.tcsw.ventas.application.ports.out.ProductoRepository;
import mx.uv.tcsw.ventas.domain.ExistenciaInsuficienteException;
import mx.uv.tcsw.ventas.domain.Producto;
import mx.uv.tcsw.ventas.domain.Venta;

class VenderProductosServiceTest {

    private static final Clock RELOJ_FIJO = Clock.fixed(Instant.parse("2026-11-02T10:15:30.123Z"), ZoneOffset.UTC);

    private ProductoRepository productos;
    private RegistrarVentaUseCase registrar;
    private VenderProductosService servicio;

    @BeforeEach
    void setUp() {
        productos = mock(ProductoRepository.class);
        registrar = mock(RegistrarVentaUseCase.class);
        servicio = new VenderProductosService(productos, registrar, RELOJ_FIJO);
        when(productos.buscarPorId(1)).thenReturn(Optional.of(new Producto(1, "P-001", "Laptop X", 15000.0, 10)));
        when(productos.buscarPorId(3)).thenReturn(Optional.of(new Producto(3, "P-003", "Mouse", 450.0, 0)));
    }

    @Test
    @DisplayName("Una venta válida se registra con folio, partidas y total correctos")
    void ventaValidaSeRegistra() {
        Venta venta = servicio.ejecutar(Collections.singletonList(new ItemVenta(1, 2)));

        ArgumentCaptor<Venta> capturada = ArgumentCaptor.forClass(Venta.class);
        verify(registrar).ejecutar(capturada.capture());
        assertSame(venta, capturada.getValue());
        assertEquals("V-20261102101530123", venta.getFolio());
        assertEquals(1, venta.getDetalles().size());
        assertEquals(30000.0, venta.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Un producto inexistente lanza ProductoNoEncontradoException y no registra nada")
    void productoInexistenteNoRegistra() {
        when(productos.buscarPorId(999)).thenReturn(Optional.empty());

           List<ItemVenta> items = Collections.singletonList(new ItemVenta(999, 1));
   ProductoNoEncontradoException ex = assertThrows(ProductoNoEncontradoException.class,
           () -> servicio.ejecutar(items));

        assertEquals(999, ex.getProductoId());
        verify(registrar, never()).ejecutar(any());
    }

    @Test
    @DisplayName("Sin existencia suficiente lanza ExistenciaInsuficienteException con el id y no registra nada")
    void existenciaInsuficienteNoRegistra() {
          List<ItemVenta> items = Collections.singletonList(new ItemVenta(3, 1));
   ExistenciaInsuficienteException ex = assertThrows(ExistenciaInsuficienteException.class,
           () -> servicio.ejecutar(items));

        assertEquals(Integer.valueOf(3), ex.getProductoId());
        verify(registrar, never()).ejecutar(any());
    }

    @Test
    @DisplayName("Una lista vacía o nula se rechaza")
    void listaVaciaSeRechaza() {
           List<ItemVenta> vacia = Collections.emptyList();
   assertThrows(IllegalArgumentException.class, () -> servicio.ejecutar(vacia));
        verify(registrar, never()).ejecutar(any());
    }

    @Test
    @DisplayName("Si la base detecta falta de existencia, se expone la causa sin el envoltorio")
    void faltaDeExistenciaEnBaseSeDesenvuelve() {
        ExistenciaInsuficienteException causa = new ExistenciaInsuficienteException("en base de datos", 1);
        doThrow(new IllegalStateException("No se pudo registrar la venta", causa)).when(registrar).ejecutar(any());

           List<ItemVenta> items = Collections.singletonList(new ItemVenta(1, 1));
   ExistenciaInsuficienteException ex = assertThrows(ExistenciaInsuficienteException.class,
           () -> servicio.ejecutar(items));

        assertSame(causa, ex);
    }

    @Test
    @DisplayName("Otros errores de registro se propagan sin cambios")
    void otrosErroresSePropagan() {
        IllegalStateException original = new IllegalStateException("fallo de conexión");
        doThrow(original).when(registrar).ejecutar(any());

           List<ItemVenta> items = Arrays.asList(new ItemVenta(1, 1));
   IllegalStateException ex = assertThrows(IllegalStateException.class,
           () -> servicio.ejecutar(items));

        assertSame(original, ex);
    }

    @Test
    @DisplayName("El constructor sin reloj genera un folio con el formato V-yyyyMMddHHmmssSSS")
    void folioConRelojDelSistema() {
        VenderProductosService conRelojReal = new VenderProductosService(productos, registrar);

        Venta venta = conRelojReal.ejecutar(Collections.singletonList(new ItemVenta(1, 1)));

        assertTrue(venta.getFolio().matches("V-\\d{17}"), "Folio inesperado: " + venta.getFolio());
    }
}
