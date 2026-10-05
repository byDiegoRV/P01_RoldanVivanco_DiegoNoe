package mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class CarritoView extends JFrame {
    private JList<String> listaCarrito;
    private DefaultListModel<String> modeloLista;
    private JLabel lblTotal;
    private JButton btnPagar, btnVolver;

    public CarritoView() {
        setTitle("Carrito de Compras - P07");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); 
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());
        
        modeloLista = new DefaultListModel<>();
        listaCarrito = new JList<>(modeloLista);
        add(new JScrollPane(listaCarrito), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new GridLayout(2, 1));
        
        lblTotal = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        lblTotal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelInferior.add(lblTotal);

        JPanel panelBotones = new JPanel();
        btnVolver = new JButton("Volver al Catálogo");
        btnPagar = new JButton("Proceder al Pago");
        panelBotones.add(btnVolver);
        panelBotones.add(btnPagar);
        panelInferior.add(panelBotones);
        
        add(panelInferior, BorderLayout.SOUTH);
    }

    public JButton getBtnPagar() { return btnPagar; }
    public JButton getBtnVolver() { return btnVolver; }
    public DefaultListModel<String> getModeloLista() { return modeloLista; }
    public void setTotal(String total) { lblTotal.setText("Total: " + total); }
}