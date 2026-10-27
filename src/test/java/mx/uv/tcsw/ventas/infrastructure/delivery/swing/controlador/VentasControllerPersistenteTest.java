package mx.uv.tcsw.ventas.infrastructure.delivery.swing.controlador;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.LockSupport;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mx.uv.tcsw.ventas.application.ports.out.ProductoRepository;
import mx.uv.tcsw.ventas.application.ports.out.VentaRepository;
import mx.uv.tcsw.ventas.application.service.RegistrarVentaService;
import mx.uv.tcsw.ventas.domain.DetalleVenta;
import mx.uv.tcsw.ventas.domain.Producto;
import mx.uv.tcsw.ventas.domain.Venta;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CarritoView;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CatalogoView;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.FormularioVentaView;

/**
 * Pruebas del controlador en modo persistente. Usan repositorios en memoria (no tocan la base de datos)
 * para verificar el flujo de la interfaz: carga del catálogo, compra exitosa y compra fallida.
 * Los diálogos modales se cierran solos para que la prueba no dependa de que alguien los cierre.
 */
class VentasControllerPersistenteTest {

    private static final String COMPRA_FALLIDA = "No se pudo registrar la compra";
    private static final long ESPERA_MAXIMA_MS = 5000;
    private static final long INTERVALO_SONDEO_NS = 25_000_000L;

    private final Map<Integer, Producto> baseSimulada = new LinkedHashMap<>();
    private final List<Venta> ventasGuardadas = new ArrayList<>();
    private final List<String> dialogos = new CopyOnWriteArrayList<>();
    private boolean fallarAlGuardar;
    private Timer cerradorDeDialogos;

    private CatalogoView catalogo;
    private CarritoView carrito;
    private FormularioVentaView formulario;

    @BeforeEach
    void setUp() throws Exception {
        baseSimulada.put(1, new Producto(1, "P-001", "Laptop", 15000.0, 10));
        baseSimulada.put(2, new Producto(2, "P-002", "Mouse", 250.0, 5));
        baseSimulada.put(3, new Producto(3, "P-003", "Teclado", 1200.0, 0));
        iniciarCerradorDeDialogos();

        catalogo = new CatalogoView();
        carrito = new CarritoView();
        formulario = new FormularioVentaView();
        SwingUtilities.invokeAndWait(() -> new VentasController(
                catalogo, carrito, formulario, productosSimulados(), new RegistrarVentaService(ventasSimuladas())));
    }

    @AfterEach
    void tearDown() {
        cerradorDeDialogos.stop();
        catalogo.dispose();
        carrito.dispose();
        formulario.dispose();
    }

    @Test
    @DisplayName("El catálogo se carga desde el repositorio")
    void catalogoSeCargaDesdeElRepositorio() throws Exception {
        assertEquals(3, catalogo.getTablaProductos().getRowCount());
        assertEquals("Laptop", celdaCatalogo(0, 1));
        assertEquals("10", celdaCatalogo(0, 3));
    }

    @Test
    @DisplayName("Agregar dos veces el mismo producto lo agrupa y conserva su id de base de datos")
    void agregarProductoPersistidoConservaId() throws Exception {
        agregarAlCarrito(0, 2);

        assertEquals(1, carrito.getModeloLista().size());
        assertTrue(carrito.getModeloLista().getElementAt(0).contains("(x2)"));
        assertEquals(1, carrito.getItems().get(0).getProducto().getId());
    }

    @Test
    @DisplayName("Compra exitosa: registra la venta, limpia el carrito y recarga las existencias")
    void compraExitosaRegistraVentaYRecargaCatalogo() throws Exception {
        agregarAlCarrito(0, 2);

        confirmarCompra("Ana Perez", "1234567890123456");
        esperar(() -> "8".equals(celdaCatalogo(0, 3)));

        assertEquals(1, ventasGuardadas.size());
        Venta venta = ventasGuardadas.get(0);
        assertTrue(venta.getFolio().matches("V-\\d{17}"), "Folio inesperado: " + venta.getFolio());
        assertEquals(1, venta.getDetalles().size());
        DetalleVenta detalle = venta.getDetalles().get(0);
        assertEquals(1, detalle.getProducto().getId());
        assertEquals(2, detalle.getCantidad());
        assertTrue(carrito.getModeloLista().isEmpty());
        assertEquals("", formulario.getTxtNombre().getText());
        assertEquals("", formulario.getTxtTarjeta().getText());
        assertTrue(formulario.getBtnConfirmar().isEnabled());
        assertEquals("Confirmar Compra", formulario.getBtnConfirmar().getText());
        assertTrue(dialogos.stream().anyMatch(d -> d.contains("Compra procesada con éxito")));
    }

    @Test
    @DisplayName("Fallo al guardar: avisa al usuario, no registra la venta y conserva el carrito")
    void falloAlGuardarConservaCarritoYAvisa() throws Exception {
        agregarAlCarrito(1, 1);
        fallarAlGuardar = true;

        confirmarCompra("Ana Perez", "1234567890123456");
        esperar(() -> dialogos.stream().anyMatch(d -> d.contains(COMPRA_FALLIDA)));

        assertTrue(ventasGuardadas.isEmpty());
        assertEquals(1, carrito.getModeloLista().size());
        assertTrue(formulario.getBtnConfirmar().isEnabled());
        esperar(() -> "5".equals(celdaCatalogo(1, 3)));
        assertFalse(dialogos.stream().anyMatch(d -> d.toLowerCase().contains("sql")),
                "El mensaje al usuario no debe exponer detalles de SQL");
    }

    @Test
    @DisplayName("Si la existencia cambió en la base después de mostrar el catálogo, la compra se rechaza")
    void existenciaCambiadaEnBaseRechazaLaCompra() throws Exception {
        agregarAlCarrito(0, 2);
        baseSimulada.put(1, new Producto(1, "P-001", "Laptop", 15000.0, 1)); // otro usuario vendió casi la totalidad de las existencias

        confirmarCompra("Ana Perez", "1234567890123456");
        esperar(() -> dialogos.stream().anyMatch(d -> d.contains(COMPRA_FALLIDA)));
        esperar(() -> "1".equals(celdaCatalogo(0, 3)));

        assertTrue(ventasGuardadas.isEmpty());
        assertEquals(1, carrito.getModeloLista().size());
    }

    @Test
    @DisplayName("Si el producto ya no existe en la base, la compra se rechaza")
    void productoEliminadoRechazaLaCompra() throws Exception {
        agregarAlCarrito(0, 1);
        baseSimulada.remove(1);

        confirmarCompra("Ana Perez", "1234567890123456");
        esperar(() -> dialogos.stream().anyMatch(d -> d.contains(COMPRA_FALLIDA)));

        assertTrue(ventasGuardadas.isEmpty());
        assertTrue(dialogos.stream().anyMatch(d -> d.contains("ya no existe")));
    }

    @Test
    @DisplayName("Con datos de pago inválidos no se intenta registrar la venta")
    void datosInvalidosNoRegistranVenta() throws Exception {
        agregarAlCarrito(0, 1);

        confirmarCompra("Ana123", "1234567890123456");
        confirmarCompra("Ana Perez", "123");

        assertTrue(ventasGuardadas.isEmpty());
        assertEquals(1, carrito.getModeloLista().size());
        esperar(() -> dialogos.stream().anyMatch(d -> d.contains("nombre válido")));
        esperar(() -> dialogos.stream().anyMatch(d -> d.contains("16 dígitos")));
    }

    // ---------- ayudas ----------

    private void agregarAlCarrito(int fila, int veces) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            catalogo.getTablaProductos().setRowSelectionInterval(fila, fila);
            for (int i = 0; i < veces; i++) {
                catalogo.getBtnAgregarCarrito().doClick();
            }
        });
    }

    private void confirmarCompra(String nombre, String tarjeta) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            carrito.getBtnPagar().doClick();
            formulario.getTxtNombre().setText(nombre);
            formulario.getTxtTarjeta().setText(tarjeta);
            formulario.getBtnConfirmar().doClick();
        });
    }

    @FunctionalInterface
    private interface Condicion {
        boolean cumple() throws Exception;
    }

    private static void esperar(Condicion condicion) throws Exception {
        long limite = System.currentTimeMillis() + ESPERA_MAXIMA_MS;
        while (!condicion.cumple() && System.currentTimeMillis() < limite) {
            LockSupport.parkNanos(INTERVALO_SONDEO_NS);
        }
        assertTrue(condicion.cumple(), "La condición esperada no se cumplió a tiempo");
    }

    private ProductoRepository productosSimulados() {
        return new ProductoRepository() {
            @Override public void guardar(Producto producto) { throw new UnsupportedOperationException(); }
            @Override public Optional<Producto> buscarPorId(Integer id) { return Optional.ofNullable(baseSimulada.get(id)).map(this::copia); }
            @Override public Optional<Producto> buscarPorCodigo(String codigo) { return Optional.empty(); }
            @Override public List<Producto> listarTodos() {
                List<Producto> copias = new ArrayList<>();
                for (Producto p : baseSimulada.values()) {
                    copias.add(copia(p));
                }
                return copias;
            }
            @Override public void actualizar(Producto producto) { throw new UnsupportedOperationException(); }
            @Override public void eliminar(Integer id) { throw new UnsupportedOperationException(); }

            private Producto copia(Producto p) {
                return new Producto(p.getId(), p.getCodigo(), p.getNombre(), p.getPrecio(), p.getExistencia());
            }
        };
    }

    private VentaRepository ventasSimuladas() {
        return venta -> {
            if (fallarAlGuardar) {
                throw new IllegalStateException("No se pudo registrar la venta " + venta.getFolio());
            }
            ventasGuardadas.add(venta);
            for (DetalleVenta d : venta.getDetalles()) {
                Producto p = baseSimulada.get(d.getProducto().getId());
                baseSimulada.put(p.getId(), new Producto(p.getId(), p.getCodigo(), p.getNombre(), p.getPrecio(),
                        p.getExistencia() - d.getCantidad()));
            }
        };
    }

    /**
     * Cierra cualquier JOptionPane que aparezca y guarda su mensaje. Se ejecuta en el hilo de eventos
     * (javax.swing.Timer sigue disparando mientras un diálogo modal bloquea el flujo normal), de modo
     * que nunca toca componentes Swing desde otro hilo.
     */
    private void iniciarCerradorDeDialogos() {
        cerradorDeDialogos = new Timer(30, e -> {
            for (Window ventana : Window.getWindows()) {
                if (ventana instanceof JDialog && ventana.isVisible()) {
                    JOptionPane panel = buscarPanel(((JDialog) ventana).getContentPane());
                    if (panel != null) {
                        dialogos.add(String.valueOf(panel.getMessage()));
                        ventana.dispose();
                    }
                }
            }
        });
        cerradorDeDialogos.start();
    }

    /** Lee una celda de la tabla del catálogo desde el hilo de eventos. */
    private String celdaCatalogo(int fila, int columna) throws Exception {
        String[] valor = new String[1];
        SwingUtilities.invokeAndWait(() -> {
            JTable tabla = catalogo.getTablaProductos();
            valor[0] = fila < tabla.getRowCount() && columna < tabla.getColumnCount()
                    ? String.valueOf(tabla.getValueAt(fila, columna)) : null;
        });
        return valor[0];
    }

    private static JOptionPane buscarPanel(Container contenedor) {
        for (Component hijo : contenedor.getComponents()) {
            if (hijo instanceof JOptionPane) {
                return (JOptionPane) hijo;
            }
            if (hijo instanceof Container) {
                JOptionPane encontrado = buscarPanel((Container) hijo);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }
}