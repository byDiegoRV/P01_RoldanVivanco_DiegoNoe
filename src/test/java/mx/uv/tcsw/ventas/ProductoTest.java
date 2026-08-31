package mx.uv.tcsw.ventas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * Pruebas automatizadas para la entidad Producto.
 *
 * <p>Incluye casos positivos (creacion y operaciones validas), casos
 * negativos o limite (datos invalidos que deben ser rechazados por los
 * invariantes de la clase) y una prueba de preservacion de estado: un
 * rechazo nunca debe dejar el objeto parcialmente modificado, tal como lo
 * exige el saber heuristico de M01/P01 ("conserva existencia despues de un
 * rechazo").</p>
 */
class ProductoTest {

    // ---------- Casos positivos: creacion valida ----------

    @Test
    void creaProductoValidoYExponeSusAtributos() {
        Producto producto = new Producto("P-001", "Teclado mecanico", 899.90, 15);

        assertEquals("P-001", producto.getCodigo());
        assertEquals("Teclado mecanico", producto.getNombre());
        assertEquals(899.90, producto.getPrecio());
        assertEquals(15, producto.getExistencia());
    }

    @Test
    void permiteExistenciaYPrecioEnCero() {
        Producto producto = new Producto("P-002", "Mouse", 0.0, 0);

        assertEquals(0.0, producto.getPrecio());
        assertEquals(0, producto.getExistencia());
    }

    @Test
    void actualizarPrecioConValorValidoLoModifica() {
        Producto producto = new Producto("P-003", "Monitor", 3200.0, 5);

        producto.actualizarPrecio(2999.0);

        assertEquals(2999.0, producto.getPrecio());
    }

    @Test
    void agregarExistenciaIncrementaElInventario() {
        Producto producto = new Producto("P-004", "Bocina", 450.0, 3);

        producto.agregarExistencia(7);

        assertEquals(10, producto.getExistencia());
    }

    @Test
    void descontarCantidadDisponibleReduceLaExistencia() {
        Producto producto = new Producto("P-005", "Webcam", 550.0, 10);

        producto.descontar(4);

        assertEquals(6, producto.getExistencia());
    }

        @Test
    void precioConDescuentoCalculaCorrectamente() {
        Producto producto = new Producto("P-005b", "Audifonos", 1000.0, 5);

        double resultado = producto.precioConDescuento(20);

        assertEquals(800.0, resultado);
    }

    @Test
    void precioConDescuentoEnCeroRegresaElMismoPrecio() {
        Producto producto = new Producto("P-005c", "Cargador", 300.0, 5);

        double resultado = producto.precioConDescuento(0);

        assertEquals(300.0, resultado);
    }

    @Test
    void precioConDescuentoEnCienRegresaCero() {
        Producto producto = new Producto("P-005d", "Funda", 250.0, 5);

        double resultado = producto.precioConDescuento(100);

        assertEquals(0.0, resultado);
    }

    // ---------- Casos negativos / limite: rechazo de datos invalidos ----------

    @Test
    void rechazaCodigoNuloAlCrear() {
        assertThrows(IllegalArgumentException.class,
                () -> new Producto(null, "Nombre valido", 10.0, 1));
    }

    @Test
    void rechazaCodigoEnBlancoAlCrear() {
        assertThrows(IllegalArgumentException.class,
                () -> new Producto("   ", "Nombre valido", 10.0, 1));
    }

    @Test
    void rechazaNombreNuloAlCrear() {
        assertThrows(IllegalArgumentException.class,
                () -> new Producto("P-006", null, 10.0, 1));
    }

    @Test
    void rechazaPrecioNegativoAlCrear() {
        assertThrows(IllegalArgumentException.class,
                () -> new Producto("P-007", "Producto invalido", -1.0, 1));
    }

    @Test
    void rechazaExistenciaNegativaAlCrear() {
        assertThrows(IllegalArgumentException.class,
                () -> new Producto("P-008", "Producto invalido", 10.0, -5));
    }

    @Test
    void rechazaActualizarPrecioConValorNegativo() {
        Producto producto = new Producto("P-009", "Impresora", 1500.0, 2);

        assertThrows(IllegalArgumentException.class,
                () -> producto.actualizarPrecio(-100.0));
    }

    @Test
    void rechazaDescontarCantidadCeroOMenor() {
        Producto producto = new Producto("P-010a", "Router", 750.0, 2);

        assertThrows(IllegalArgumentException.class,
                () -> producto.descontar(0));
    }

    @Test
    void rechazaDescontarMasExistenciaDeLaDisponible() {
        Producto producto = new Producto("P-010", "Router", 750.0, 2);

        assertThrows(IllegalStateException.class,
                () -> producto.descontar(5));
    }

    @Test
    void rechazaAgregarExistenciaConCantidadNoPositiva() {
        Producto producto = new Producto("P-011", "Cable HDMI", 120.0, 20);

        assertThrows(IllegalArgumentException.class,
                () -> producto.agregarExistencia(0));
    }

        @Test
    void rechazaPrecioConDescuentoNegativo() {
        Producto producto = new Producto("P-011b", "Micrófono", 800.0, 3);

        assertThrows(IllegalArgumentException.class,
                () -> producto.precioConDescuento(-5));
    }

    @Test
    void rechazaPrecioConDescuentoMayorA100() {
        // este caso es justo el que provoco el conflicto entre las dos ramas:
        // una version no validaba este limite y podia regresar un precio negativo
        Producto producto = new Producto("P-011c", "Tripie", 450.0, 3);

        assertThrows(IllegalArgumentException.class,
                () -> producto.precioConDescuento(150));
    }

    // ---------- Preservacion de estado tras un rechazo ----------

    @Test
    void conservaExistenciaDespuesDeUnDescuentoRechazadoPorLimite() {
        Producto producto = new Producto("P-012", "Teclado inalambrico", 899.0, 3);

        assertThrows(IllegalStateException.class, () -> producto.descontar(4));

        assertEquals(3, producto.getExistencia(),
                "Un descuento rechazado por existencia insuficiente no debe modificar el estado.");
    }

    @Test
    void conservaExistenciaDespuesDeUnDescuentoRechazadoPorArgumentoInvalido() {
        Producto producto = new Producto("P-013", "Mousepad", 199.0, 8);

        assertThrows(IllegalArgumentException.class, () -> producto.descontar(-1));

        assertEquals(8, producto.getExistencia(),
                "Un descuento con cantidad invalida no debe modificar el estado.");
    }

    @Test
    void conservaPrecioDespuesDeUnaActualizacionRechazada() {
        Producto producto = new Producto("P-014", "Silla gamer", 4500.0, 1);

        assertThrows(IllegalArgumentException.class, () -> producto.actualizarPrecio(-1.0));

        assertEquals(4500.0, producto.getPrecio(),
                "Un precio invalido rechazado no debe modificar el estado.");
    }
}
