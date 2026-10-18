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

public class CarritoView extends JFrame {
    private DefaultListModel<String> modeloLista;
    private JList<String> listaCarrito;
    private JLabel lblTotal;
    private JButton btnVolver;
    private JButton btnPagar;
    
    // Mapa para llevar el control de cantidades y subtotales por producto
    // 1. Añade "transient" al mapa
    private transient Map<String, CarritoItemControl> itemsCarrito = new HashMap<>();
    private double totalGeneral = 0.0;
    private DecimalFormat df = new DecimalFormat("$#,##0.00");

    public CarritoView() {
        setTitle("Carrito de Compras - P07");
        setSize(450, 350);
        setLocationRelativeTo(null);
        // 2. Usa WindowConstants en lugar de JFrame
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLayout(new BorderLayout());

        modeloLista = new DefaultListModel<>();
        listaCarrito = new JList<>(modeloLista);
        add(new JScrollPane(listaCarrito), BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new BorderLayout());
        lblTotal = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        panelSur.add(lblTotal, BorderLayout.NORTH);

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
            CarritoItemControl item = itemsCarrito.get(nombre);
            item.cantidad++;
            item.subtotal += precio;
        } else {
            itemsCarrito.put(nombre, new CarritoItemControl(nombre, 1, precio));
        }
        actualizarVistaLista();
    }

    private void actualizarVistaLista() {
        modeloLista.clear();
        totalGeneral = 0.0;

        for (CarritoItemControl item : itemsCarrito.values()) {
            String texto = String.format("%s (x%d) | %s", item.nombre, item.cantidad, df.format(item.subtotal));
            modeloLista.addElement(texto);
            totalGeneral += item.subtotal;
        }

        lblTotal.setText("Total: " + df.format(totalGeneral));
    }

    public void limpiarCarrito() {
        itemsCarrito.clear();
        modeloLista.clear();
        totalGeneral = 0.0;
        lblTotal.setText("Total: $0.00");
    }

    // Getters para controladores y pruebas
    public JButton getBtnVolver() { return btnVolver; }
    public JButton getBtnPagar() { return btnPagar; }
    public DefaultListModel<String> getModeloLista() { return modeloLista; }
    public double getTotalGeneral() { return totalGeneral; }

    // Clase auxiliar interna para el control agrupado
    private static class CarritoItemControl {
        String nombre;
        int cantidad;
        double subtotalUnitario;
        double subtotal;

        public CarritoItemControl(String nombre, int cantidad, double precioUnitario) {
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.subtotalUnitario = precioUnitario;
            this.subtotal = precioUnitario;
        }
    }
}