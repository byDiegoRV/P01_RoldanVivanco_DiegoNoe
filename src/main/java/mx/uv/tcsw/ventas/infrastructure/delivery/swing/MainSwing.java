package mx.uv.tcsw.ventas.infrastructure.delivery.swing;

import javax.swing.SwingUtilities;

import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CatalogoView;

public class MainSwing {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CatalogoView().setVisible(true);
        });
    }
}