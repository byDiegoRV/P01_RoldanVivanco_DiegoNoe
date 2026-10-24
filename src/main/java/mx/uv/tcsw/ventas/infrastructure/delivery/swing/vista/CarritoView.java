package mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import mx.uv.tcsw.ventas.domain.Producto;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.modelo.CarritoViewModel;

public class CarritoView extends JFrame {
    private DefaultListModel<String> modeloLista;
    private JList<String> listaCarrito;
    private JLabel lblTotal;
    private JButton btnVolver;
    private JButton btnPagar;
    
    // Botones de control para el carrito
    private JButton btnIncrementar;
    private JButton btnDecrementar;
    private JButton btnQuitar;
    
    // Mapa usando tu CarritoViewModel existente
    private transient Map<String, CarritoViewModel> itemsCarrito = new HashMap<>();
    private double totalGeneral = 0.0;
    private DecimalFormat df = new DecimalFormat("$#,##0.00");

    public CarritoView() {
        setTitle("Carrito de Compras - P07");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLayout(new BorderLayout());

        modeloLista = new DefaultListModel<>();
        listaCarrito = new JList<>(modeloLista);
        add(new JScrollPane(listaCarrito), BorderLayout.CENTER);

        // Panel inferior para controles y pagos
        JPanel panelSur = new JPanel(new BorderLayout());
        
        // Panel con los botones de acción por producto (+, -, Quitar)
        JPanel panelAccionesItem = new JPanel();
        btnIncrementar = new JButton("+");
        btnDecrementar = new JButton("-");
        btnQuitar = new JButton("Quitar del Carrito");
        
        // Enlazar eventos
        btnIncrementar.addActionListener(e -> incrementarCantidadSeleccionada());
        btnDecrementar.addActionListener(e -> decrementarCantidadSeleccionada());
        btnQuitar.addActionListener(e -> quitarProductoSeleccionado());
        
        panelAccionesItem.add(btnDecrementar);
        panelAccionesItem.add(btnIncrementar);
        panelAccionesItem.add(btnQuitar);
        panelSur.add(panelAccionesItem, BorderLayout.NORTH);

        // Total general
        lblTotal = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        panelSur.add(lblTotal, BorderLayout.CENTER);

        // Botones de navegación principales (Volver / Pagar)
        JPanel panelBotones = new JPanel();
        btnVolver = new JButton("Volver al Catálogo");
        btnPagar = new JButton("Proceder al Pago");
        panelBotones.add(btnVolver);
        panelBotones.add(btnPagar);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        add(panelSur, BorderLayout.SOUTH);
    }

   public void agregarProducto(String nombre, double precio) {
        if (itemsCarrito.containsKey(nombre)) {
            CarritoViewModel item = itemsCarrito.get(nombre);
            item.setCantidad(item.getCantidad() + 1);
        } else {
            //  Pasamos un código válido en lugar de null para cumplir con la regla del dominio
            Producto producto = new Producto("SKU-" + nombre, nombre, precio, 100);
            itemsCarrito.put(nombre, new CarritoViewModel(producto, 1));
        }
        actualizarVistaLista();
    }

    public void incrementarCantidadSeleccionada() {
        String seleccionado = listaCarrito.getSelectedValue();
        if (seleccionado != null) {
            String nombre = extraerNombreDesdeTexto(seleccionado);
            CarritoViewModel item = itemsCarrito.get(nombre);
            if (item != null) {
                item.setCantidad(item.getCantidad() + 1);
                actualizarVistaLista();
            }
        }
    }

    public void decrementarCantidadSeleccionada() {
        String seleccionado = listaCarrito.getSelectedValue();
        if (seleccionado != null) {
            String nombre = extraerNombreDesdeTexto(seleccionado);
            CarritoViewModel item = itemsCarrito.get(nombre);
            if (item != null && item.getCantidad() > 1) {
                item.setCantidad(item.getCantidad() - 1);
                actualizarVistaLista();
            }
        }
    }

    public void quitarProductoSeleccionado() {
        String seleccionado = listaCarrito.getSelectedValue();
        if (seleccionado != null) {
            String nombre = extraerNombreDesdeTexto(seleccionado);
            itemsCarrito.remove(nombre);
            actualizarVistaLista();
        }
    }

    private String extraerNombreDesdeTexto(String textoLista) {
        int idx = textoLista.indexOf(" (x");
        return (idx != -1) ? textoLista.substring(0, idx) : textoLista;
    }

    private void actualizarVistaLista() {
        String seleccionadoPrevio = listaCarrito.getSelectedValue();
        modeloLista.clear();
        totalGeneral = 0.0;

        for (CarritoViewModel item : itemsCarrito.values()) {
            if (item.isSeleccionado()) {
                String nombreProd = item.getProducto().getNombre();
                String texto = String.format("%s (x%d) | %s", nombreProd, item.getCantidad(), df.format(item.getSubtotal()));
                modeloLista.addElement(texto);
                totalGeneral += item.getSubtotal();
            }
        }

        lblTotal.setText("Total: " + df.format(totalGeneral));
        
        if (seleccionadoPrevio != null && modeloLista.contains(seleccionadoPrevio)) {
            listaCarrito.setSelectedValue(seleccionadoPrevio, true);
        }
    }

    public void limpiarCarrito() {
        itemsCarrito.clear();
        modeloLista.clear();
        totalGeneral = 0.0;
        lblTotal.setText("Total: $0.00");
    }

    // Getters
    public JButton getBtnVolver() { return btnVolver; }
    public JButton getBtnPagar() { return btnPagar; }
    public JButton getBtnIncrementar() { return btnIncrementar; }
    public JButton getBtnDecrementar() { return btnDecrementar; }
    public JButton getBtnQuitar() { return btnQuitar; }
    public JList<String> getListaCarrito() { return listaCarrito; }
    public DefaultListModel<String> getModeloLista() { return modeloLista; }
    public double getTotalGeneral() { return totalGeneral; }
}