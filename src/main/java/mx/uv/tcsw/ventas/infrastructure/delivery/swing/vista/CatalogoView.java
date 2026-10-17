package mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

public class CatalogoView extends JFrame {
    private JTable tablaProductos;
    private JButton btnAgregarCarrito;
    private JButton btnVerCarrito;

    public CatalogoView() {
        setTitle("Catálogo de Productos - P07");
        setSize(600, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        String[] columnas = {"ID", "Nombre", "Precio"};
        String[][] datos = {
            {"1", "Laptop X", "$15000"}, 
            {"2", "Teclado Mecánico", "$1200"},
            {"3", "Mouse Inalámbrico", "$450"},
            {"4", "Monitor LED 24\"", "$3200"},
            {"5", "Audífonos Gamer", "$850"},
            {"6", "Silla Ergonómica", "$2100"}
        };
        
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


