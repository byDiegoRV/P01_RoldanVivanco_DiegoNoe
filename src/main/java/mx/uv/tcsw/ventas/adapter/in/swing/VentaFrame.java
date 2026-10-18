package mx.uv.tcsw.ventas.adapter.in.swing;

import javax.swing.*;
import java.awt.*;

public class VentaFrame extends JFrame {
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JButton btnGuardar;
    private JLabel lblEstado;

    public VentaFrame() {
        setTitle("Módulo de Ventas - Cliente Swing");
        setSize(400, 250);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 5, 5));

        add(new JLabel("Nombre Producto:"));
        txtNombre = new JTextField();
        add(txtNombre);

        add(new JLabel("Precio:"));
        txtPrecio = new JTextField();
        add(txtPrecio);

        btnGuardar = new JButton("Guardar");
        add(btnGuardar);

        lblEstado = new JLabel("Estado: Listo");
        add(lblEstado);

        btnGuardar.addActionListener(e -> guardarProducto());
    }

    private void guardarProducto() {
        // 1. Validar campo obligatorio (Presencia de datos)
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, 
                "El nombre del producto es obligatorio. Por favor ingrese un valor.", 
                "Validación de Entrada", 
                JOptionPane.WARNING_MESSAGE
            );
            txtNombre.requestFocusInWindow(); // Accesibilidad: Deplaza el foco al campo con error
            return;
        }

        // 2. Validar formato numérico (Captura de NumberFormatException)
        double precio;
        try {
            precio = Double.parseDouble(txtPrecio.getText().trim());
            if (precio <= 0) {
                JOptionPane.showMessageDialog(
                    this, 
                    "El precio debe ser un número mayor a cero.", 
                    "Formato Inválido", 
                    JOptionPane.WARNING_MESSAGE
                );
                txtPrecio.requestFocusInWindow(); // Accesibilidad: Retorna el foco al campo numérico
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this, 
                "El valor ingresado en 'Precio' no es un número válido. Ejemplo: 150.50", 
                "Error de Formato", 
                JOptionPane.ERROR_MESSAGE
            );
            txtPrecio.requestFocusInWindow(); // Accesibilidad: Retorna el foco al campo numérico
            return;
        }

        lblEstado.setText("Producto " + nombre + " validado correctamente.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentaFrame frame = new VentaFrame();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
