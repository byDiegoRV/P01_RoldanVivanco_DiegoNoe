package mx.uv.tcsw.ventas.application.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.adapter.out.persistence.VentaRepositoryInMemory;
import mx.uv.tcsw.ventas.domain.Producto;
import mx.uv.tcsw.ventas.domain.Venta;

class RegistrarVentaServiceTest {

    private RegistrarVentaService registrarVentaService;
    private VentaRepositoryInMemory ventaRepositoryInMemory;

    @BeforeEach
    void setUp() {
        ventaRepositoryInMemory = new VentaRepositoryInMemory();
        registrarVentaService = new RegistrarVentaService(ventaRepositoryInMemory);
    }

    @Test
    void deberiaRegistrarVentaExitosamente() {
        Venta venta = new Venta("F001");
        Producto producto = new Producto("P1", "Laptop", 1000.0, 5);
        venta.agregarPartida(producto, 1);

        assertDoesNotThrow(() -> registrarVentaService.ejecutar(venta));
        assertEquals(1, ventaRepositoryInMemory.obtenerTodas().size());
        assertEquals("F001", ventaRepositoryInMemory.obtenerTodas().get(0).getFolio());
    }

    @Test
    void deberiaLanzarExcepcionSiLaVentaNoTieneDetalles() {
        Venta ventaVacia = new Venta("F002");

        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> registrarVentaService.ejecutar(ventaVacia)
        );

        assertEquals("La venta debe contener al menos un producto.", excepcion.getMessage());
        assertTrue(ventaRepositoryInMemory.obtenerTodas().isEmpty());
    }
}