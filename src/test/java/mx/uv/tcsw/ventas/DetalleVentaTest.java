package mx.uv.tcsw.ventas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * Pruebas automatizadas para la entidad DetalleVenta.
 *
 * <p>Incluye casos positivos (creacion valida y calculo de subtotal) y
 * casos negativos o limite (datos invalidos que deben ser rechazados por
 * los invariantes de la clase), siguiendo el mismo criterio usado en
 * ProductoTest (P01).</p>
 */
class DetalleVentaTest {

    private static Producto productoValido() {
        return new Producto("P-001", "Teclado mecanico", 899.90, 15);
    }

    // ---------- Casos positivos ----------

    @Test
    void creaDetalleValidoYExponeSusAtributos() {
        Producto producto = productoValido();
        DetalleVenta detalle = new DetalleVenta(producto, 3, 899.90);

        assertEquals(producto, detalle.getProducto());
        assertEquals(3, detalle.getCantidad());
        assertEquals(899.90, detalle.getPrecioUnitario());
    }

    @Test
    void calculaSubtotalComoCantidadPorPrecio() {
        DetalleVenta detalle = new DetalleVenta(productoValido(), 3, 100.0);

        assertEquals(300.0, detalle.subtotal());
    }

    @Test
    void permitePrecioUnitarioEnCero() {
        DetalleVenta detalle = new DetalleVenta(productoValido(), 2, 0.0);

        assertEquals(0.0, detalle.subtotal());
    }

    @Test
    void congelaElPrecioAunqueElProductoCambieDespues() {
        Producto producto = productoValido();
        DetalleVenta detalle = new DetalleVenta(producto, 1, producto.getPrecio());

        producto.actualizarPrecio(1500.0);

        assertEquals(899.90, detalle.getPrecioUnitario(),
                "El precio del detalle no debe cambiar aunque el producto cambie despues.");
    }

    @Test
    void dosDetallesConMismosDatosSonIguales() {
        Producto producto = productoValido();
        DetalleVenta uno = new DetalleVenta(producto, 2, 899.90);
        DetalleVenta otro = new DetalleVenta(producto, 2, 899.90);

        assertEquals(uno, otro);
        assertEquals(uno.hashCode(), otro.hashCode());
    }

    // ---------- Casos negativos / limite ----------
  @Test
    void rechazaProductoNuloAlCrear() {
        assertThrows(IllegalArgumentException.class,
                () -> new DetalleVenta(null, 1, 10.0));
    }
    
   @Test
void rechazaCantidadCeroAlCrear() {
    Producto producto = productoValido();

    assertThrows(IllegalArgumentException.class,
            () -> new DetalleVenta(producto, 0, 10.0));
}

@Test
void rechazaCantidadNegativaAlCrear() {
    Producto producto = productoValido();

    assertThrows(IllegalArgumentException.class,
            () -> new DetalleVenta(producto, -1, 10.0));
}

@Test
void rechazaPrecioNegativoAlCrear() {
    Producto producto = productoValido();

    assertThrows(IllegalArgumentException.class,
            () -> new DetalleVenta(producto, 1, -0.01));
}
}
