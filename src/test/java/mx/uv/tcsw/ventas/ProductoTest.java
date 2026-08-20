package mx.uv.tcsw.ventas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas automatizadas para la entidad Producto.
 *
 * <p>Incluye casos positivos (creacion y operaciones validas) y casos
 * negativos o limite (datos invalidos que deben ser rechazados por los
 * invariantes de la clase), tal como lo exige el saber heuristico de P01.</p>
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
    void retirarExistenciaDentroDelLimiteDisponibleLaReduce() {
        Producto producto = new Producto("P-005", "Webcam", 550.0, 10);

        producto.retirarExistencia(4);

        assertEquals(6, producto.getExistencia());
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
    void rechazaRetirarMasExistenciaDeLaDisponible() {
        Producto producto = new Producto("P-010", "Router", 750.0, 2);

        assertThrows(IllegalArgumentException.class,
                () -> producto.retirarExistencia(5));
    }

    @Test
    void rechazaAgregarExistenciaConCantidadNoPositiva() {
        Producto producto = new Producto("P-011", "Cable HDMI", 120.0, 20);

        assertThrows(IllegalArgumentException.class,
                () -> producto.agregarExistencia(0));
    }
}
