package mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class FormularioVentaView extends JFrame {
    private JTextField txtNombre;
    private JTextField txtTarjeta;
    private JButton btnConfirmar;
    private JButton btnCancelar;

    public FormularioVentaView() {
        setTitle("Pago y Confirmación - P07");
        setSize(350, 200);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelFormulario = new JPanel(new GridLayout(2, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelFormulario.add(new JLabel("Nombre del Titular:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Número de Tarjeta:"));
        txtTarjeta = new JTextField();
        panelFormulario.add(txtTarjeta);

        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        btnCancelar = new JButton("Cancelar");
        btnConfirmar = new JButton("Confirmar Compra");
        panelBotones.add(btnCancelar);
        panelBotones.add(btnConfirmar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public JButton getBtnConfirmar() { return btnConfirmar; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public JTextField getTxtNombre() { return txtNombre; }
    public JTextField getTxtTarjeta() { return txtTarjeta; }
}