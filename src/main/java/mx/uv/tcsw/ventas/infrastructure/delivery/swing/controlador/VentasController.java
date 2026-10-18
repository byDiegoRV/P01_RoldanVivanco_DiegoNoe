package mx.uv.tcsw.ventas.infrastructure.delivery.swing.controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CarritoView;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CatalogoView;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.FormularioVentaView;

public class VentasController implements ActionListener {
    private static final Logger LOGGER = Logger.getLogger(VentasController.class.getName());
    
    private CatalogoView catalogoView;
    private CarritoView carritoView;
    private FormularioVentaView formularioView;
    private double totalCarrito = 0.0;

    public VentasController(CatalogoView catalogoView, CarritoView carritoView, FormularioVentaView formularioView) {
        this.catalogoView = catalogoView;
        this.carritoView = carritoView;
        this.formularioView = formularioView;
        inicializarEventos();
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
        if (totalCarrito == 0) {
            JOptionPane.showMessageDialog(carritoView, "El carrito está vacío. Agrega productos primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        formularioView.setVisible(true);
    }

    private void agregarProductoAlCarrito() {
        int filaSeleccionada = catalogoView.getTablaProductos().getSelectedRow();
        
        if (filaSeleccionada == -1) { 
            JOptionPane.showMessageDialog(catalogoView, "Por favor, selecciona un producto de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = (String) catalogoView.getTablaProductos().getValueAt(filaSeleccionada, 1);
        String precioStr = (String) catalogoView.getTablaProductos().getValueAt(filaSeleccionada, 2);
        
        try {
            double precio = Double.parseDouble(precioStr.replace("$", "").trim());
            
            // Agrupa y actualiza la lista y el total automáticamente en la vista
            carritoView.agregarProducto(nombre, precio);
            
            // Sincronizamos el acumulador general del controlador
            totalCarrito += precio;
            
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.SEVERE, "Error al calcular el precio del producto", ex);
        }
        
        JOptionPane.showMessageDialog(catalogoView, nombre + " agregado al carrito.");
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

        SwingWorker<Boolean, Void> operacionBackground = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Thread.sleep(2000); 
                return true; 
            }

            @Override
            protected void done() {
                formularioView.getBtnConfirmar().setEnabled(true);
                formularioView.getBtnConfirmar().setText("Confirmar Compra");
                JOptionPane.showMessageDialog(formularioView, "¡Compra procesada con éxito por $" + String.format("%.2f", totalCarrito) + "!");
                
                formularioView.setVisible(false);
                carritoView.setVisible(false);
                
                // Limpiamos el carrito utilizando el método dedicado de la vista
                carritoView.limpiarCarrito();
                totalCarrito = 0.0;
                
                formularioView.getTxtNombre().setText("");
                formularioView.getTxtTarjeta().setText("");
            }
        };
        operacionBackground.execute();
    }
}