package mx.uv.tcsw.ventas.infrastructure.delivery.swing;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.hibernate.SessionFactory;

import mx.uv.tcsw.ventas.application.ports.out.ProductoRepository;
import mx.uv.tcsw.ventas.application.ports.out.VentaRepository;
import mx.uv.tcsw.ventas.application.service.RegistrarVentaService;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.controlador.VentasController;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CarritoView;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.CatalogoView;
import mx.uv.tcsw.ventas.infrastructure.delivery.swing.vista.FormularioVentaView;
import mx.uv.tcsw.ventas.infrastructure.persistence.ConfiguracionBaseDeDatos;
import mx.uv.tcsw.ventas.infrastructure.persistence.hibernate.HibernateProductoAdapter;
import mx.uv.tcsw.ventas.infrastructure.persistence.hibernate.HibernateSessionFactoryProvider;
import mx.uv.tcsw.ventas.infrastructure.persistence.hibernate.HibernateVentaAdapter;
import mx.uv.tcsw.ventas.infrastructure.persistence.jdbc.DatabaseConnectionManager;
import mx.uv.tcsw.ventas.infrastructure.persistence.jdbc.PostgresProductoAdapter;
import mx.uv.tcsw.ventas.infrastructure.persistence.jdbc.VentaJdbcRepository;

/** Punto de ensamblaje: aquí (y solo aquí) se elige el adaptador de persistencia. */
public class MainSwing {
    private static final Logger LOGGER = Logger.getLogger(MainSwing.class.getName());

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                iniciar();
            } catch (RuntimeException ex) {
                LOGGER.log(Level.SEVERE, "No se pudo iniciar la aplicación", ex);
                JOptionPane.showMessageDialog(null,
                        "No se pudo conectar con la base de datos.\nRevisa DB_URL, DB_USER y DB_PASSWORD.",
                        "Error de inicio", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void iniciar() {
        String url = ConfiguracionBaseDeDatos.url();
        String usuario = ConfiguracionBaseDeDatos.usuario();
        String password = ConfiguracionBaseDeDatos.password();

        ProductoRepository productos;
        VentaRepository ventas;
        if ("jdbc".equals(ConfiguracionBaseDeDatos.modoPersistencia())) {
            DatabaseConnectionManager conexion = new DatabaseConnectionManager(url, usuario, password);
            productos = new PostgresProductoAdapter(conexion);
            ventas = new VentaJdbcRepository(conexion);
        } else {
            SessionFactory sessionFactory = HibernateSessionFactoryProvider.crear(url, usuario, password);
            productos = new HibernateProductoAdapter(sessionFactory);
            ventas = new HibernateVentaAdapter(sessionFactory);
        }

        CatalogoView catalogo = new CatalogoView();
        CarritoView carrito = new CarritoView();
        FormularioVentaView formulario = new FormularioVentaView();

        new VentasController(catalogo, carrito, formulario, productos, new RegistrarVentaService(ventas));

        catalogo.setVisible(true);
    }
}
