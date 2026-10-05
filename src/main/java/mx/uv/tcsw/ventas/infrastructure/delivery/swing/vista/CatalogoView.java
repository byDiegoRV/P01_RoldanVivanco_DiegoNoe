package mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class CatalogoView extends JFrame {
    private JTable tablaProductos;
    private JButton btnAgregarCarrito, btnVerCarrito;

    public CatalogoView() {
        setTitle("Catálogo de Productos - P07");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());
        String[] columnas = {"ID", "Nombre", "Precio"};
        String[][] datos = {{"1", "Laptop X", "$15000"}, {"2", "Teclado", "$1200"}};
        
        tablaProductos = new JTable(datos, columnas);
        add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        btnAgregarCarrito = new JButton("Agregar al Carrito");
        btnVerCarrito = new JButton("Ir al Carrito");
        panelBotones.add(btnAgregarCarrito);
        panelBotones.add(btnVerCarrito);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public JButton getBtnAgregarCarrito() { return btnAgregarCarrito; }
    public JButton getBtnVerCarrito() { return btnVerCarrito; }
    public JTable getTablaProductos() { return tablaProductos; }
}
