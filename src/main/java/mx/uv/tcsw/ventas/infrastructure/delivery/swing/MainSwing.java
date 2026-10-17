package mx.uv.tcsw.ventas.infrastructure.delivery.swing;

import javax.swing.SwingUtilities;

import mx.uv.tcsw.ventas.infrastructure.delivery.swing.controlador.VentasController;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CarritoView;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CatalogoView;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.FormularioVentaView;

public class MainSwing {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CatalogoView catalogo = new CatalogoView();
            CarritoView carrito = new CarritoView();
            FormularioVentaView formulario = new FormularioVentaView();

            new VentasController(catalogo, carrito, formulario);

            catalogo.setVisible(true);
        });
    }
}