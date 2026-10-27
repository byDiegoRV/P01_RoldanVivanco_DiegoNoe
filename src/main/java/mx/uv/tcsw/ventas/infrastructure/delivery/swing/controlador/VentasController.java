package mx.uv.tcsw.ventas.infrastructure.delivery.swing.controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import mx.uv.tcsw.ventas.application.ports.in.RegistrarVentaUseCase;
import mx.uv.tcsw.ventas.application.ports.out.ProductoRepository;
import mx.uv.tcsw.ventas.domain.Producto;
import mx.uv.tcsw.ventas.domain.Venta;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.modelo.CarritoViewModel;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CarritoView;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CatalogoView;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.FormularioVentaView;

public class VentasController implements ActionListener {
    private static final Logger LOGGER = Logger.getLogger(VentasController.class.getName());
    private static final DateTimeFormatter FORMATO_FOLIO = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final String TITULO_AVISO = "Aviso";

    private final CatalogoView catalogoView;
    private final CarritoView carritoView;
    private final FormularioVentaView formularioView;

    // Puertos de salida/entrada. Si son null, el controlador funciona en modo demostración (sin base de datos).
    private final ProductoRepository productoRepository;
    private final RegistrarVentaUseCase registrarVenta;
    private final Map<Integer, Producto> productosPorId = new HashMap<>();

    /** Modo demostración: catálogo fijo y compra simulada (se conserva para las pruebas de la capa de vista). */
    public VentasController(CatalogoView catalogoView, CarritoView carritoView, FormularioVentaView formularioView) {
        this(catalogoView, carritoView, formularioView, null, null);
    }

    /** Modo persistente: el catálogo se lee del repositorio y la compra se registra con el caso de uso. */
    public VentasController(CatalogoView catalogoView, CarritoView carritoView, FormularioVentaView formularioView,
            ProductoRepository productoRepository, RegistrarVentaUseCase registrarVenta) {
        this.catalogoView = catalogoView;
        this.carritoView = carritoView;
        this.formularioView = formularioView;
        this.productoRepository = productoRepository;
        this.registrarVenta = registrarVenta;
        inicializarEventos();
        cargarCatalogo();
    }

    private boolean modoPersistente() {
        return productoRepository != null && registrarVenta != null;
    }

    private void cargarCatalogo() {
        if (!modoPersistente()) {
            return;
        }
        List<Producto> productos = productoRepository.listarTodos();
        productosPorId.clear();
        for (Producto producto : productos) {
            productosPorId.put(producto.getId(), producto);
        }
        catalogoView.cargarProductos(productos);
    }

    private void inicializarEventos() {
        catalogoView.getBtnVerCarrito().addActionListener(this);
        catalogoView.getBtnAgregarCarrito().addActionListener(this);
        carritoView.getBtnVolver().addActionListener(this);
        carritoView.getBtnPagar().addActionListener(this);
        formularioView.getBtnCancelar().addActionListener(this);
        formularioView.getBtnConfirmar().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == catalogoView.getBtnVerCarrito()) {
            carritoView.setVisible(true);
        } else if (source == carritoView.getBtnVolver()) {
            carritoView.setVisible(false);
        } else if (source == carritoView.getBtnPagar()) {
            verificarYMostrarPago();
        } else if (source == formularioView.getBtnCancelar()) {
            formularioView.setVisible(false);
        } else if (source == catalogoView.getBtnAgregarCarrito()) {
            agregarProductoAlCarrito();
        } else if (source == formularioView.getBtnConfirmar()) {
            validarYProcesarCompra();
        }
    }

    private void verificarYMostrarPago() {
        if (carritoView.getTotalGeneral() == 0) {
            JOptionPane.showMessageDialog(carritoView, "El carrito está vacío. Agrega productos primero.", TITULO_AVISO, JOptionPane.WARNING_MESSAGE);
            return;
        }
        formularioView.setVisible(true);
    }

    private void agregarProductoAlCarrito() {
        int filaSeleccionada = catalogoView.getTablaProductos().getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(catalogoView, "Por favor, selecciona un producto de la tabla.", TITULO_AVISO, JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = (String) catalogoView.getTablaProductos().getValueAt(filaSeleccionada, 1);

        if (modoPersistente()) {
            agregarProductoPersistido(filaSeleccionada, nombre);
        } else {
            agregarProductoDemostracion(filaSeleccionada, nombre);
        }

        JOptionPane.showMessageDialog(catalogoView, nombre + " agregado al carrito.");
    }

    private void agregarProductoPersistido(int fila, String nombre) {
        int id = Integer.parseInt((String) catalogoView.getTablaProductos().getValueAt(fila, 0));
        Producto producto = productosPorId.get(id);
        if (producto == null) {
            LOGGER.log(Level.SEVERE, "El producto {0} no está en el catálogo cargado", nombre);
            return;
        }
        carritoView.agregarProducto(producto);
    }

    private void agregarProductoDemostracion(int fila, String nombre) {
        String precioStr = (String) catalogoView.getTablaProductos().getValueAt(fila, 2);
        try {
            double precio = Double.parseDouble(precioStr.replace("$", "").trim());
            carritoView.agregarProducto(nombre, precio);
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.SEVERE, "Error al calcular el precio del producto", ex);
        }
    }

    private void validarYProcesarCompra() {
        String nombre = formularioView.getTxtNombre().getText().trim();
        String tarjeta = formularioView.getTxtTarjeta().getText().trim();

        if (nombre.isEmpty() || !nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            JOptionPane.showMessageDialog(formularioView, "Ingresa un nombre válido (solo letras).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tarjeta.isEmpty() || !tarjeta.matches("^\\d{16}$")) {
            JOptionPane.showMessageDialog(formularioView, "La tarjeta debe tener exactamente 16 dígitos numéricos.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        procesarCompraBackground();
    }

    private void procesarCompraBackground() {
        formularioView.getBtnConfirmar().setEnabled(false);
        formularioView.getBtnConfirmar().setText("Procesando...");
        final double total = carritoView.getTotalGeneral();

        SwingWorker<Void, Void> operacionBackground = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (modoPersistente()) {
                    registrarVenta.ejecutar(construirVenta());
                } else {
                    Thread.sleep(2000); // modo demostración
                }
                return null;
            }

            @Override
            protected void done() {
                formularioView.getBtnConfirmar().setEnabled(true);
                formularioView.getBtnConfirmar().setText("Confirmar Compra");
                try {
                    get();
                    compraExitosa(total);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    compraFallida(ex);
                } catch (ExecutionException ex) {
                    compraFallida(ex.getCause() != null ? ex.getCause() : ex);
                }
            }
        };
        operacionBackground.execute();
    }

    /** Arma la venta con datos frescos de la base: el stock se valida contra el estado real, no contra el catálogo en pantalla. */
    private Venta construirVenta() {
        Venta venta = new Venta("V-" + LocalDateTime.now().format(FORMATO_FOLIO));
        for (CarritoViewModel item : carritoView.getItems()) {
            Producto fresco = productoRepository.buscarPorId(item.getProducto().getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "El producto " + item.getProducto().getNombre() + " ya no existe en el catálogo."));
            venta.agregarPartida(fresco, item.getCantidad());
        }
        return venta;
    }

    private void compraExitosa(double total) {
        JOptionPane.showMessageDialog(formularioView, "¡Compra procesada con éxito por $" + String.format("%.2f", total) + "!");

        formularioView.setVisible(false);
        carritoView.setVisible(false);
        carritoView.limpiarCarrito();

        formularioView.getTxtNombre().setText("");
        formularioView.getTxtTarjeta().setText("");
        cargarCatalogo(); // refleja las existencias ya descontadas
    }

    private void compraFallida(Throwable causa) {
        // La causa completa va al registro; al usuario solo se le muestra un mensaje de aplicación, sin SQL ni credenciales.
        LOGGER.log(Level.SEVERE, "No se pudo registrar la venta", causa);
        JOptionPane.showMessageDialog(formularioView,
                "No se pudo registrar la compra. No se realizó ningún cambio.\n" + causa.getMessage(),
                "Error al registrar la venta", JOptionPane.ERROR_MESSAGE);
        cargarCatalogo(); // vuelve a leer el estado real de la base
    }
}
