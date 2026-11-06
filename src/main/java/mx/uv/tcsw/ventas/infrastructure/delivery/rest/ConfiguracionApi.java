package mx.uv.tcsw.ventas.infrastructure.delivery.rest;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import mx.uv.tcsw.ventas.application.ports.in.ConsultarProductosUseCase;
import mx.uv.tcsw.ventas.application.ports.in.RegistrarVentaUseCase;
import mx.uv.tcsw.ventas.application.ports.in.VenderProductosUseCase;
import mx.uv.tcsw.ventas.application.ports.out.ProductoRepository;
import mx.uv.tcsw.ventas.application.ports.out.VentaRepository;
import mx.uv.tcsw.ventas.application.service.ConsultarProductosService;
import mx.uv.tcsw.ventas.application.service.RegistrarVentaService;
import mx.uv.tcsw.ventas.application.service.VenderProductosService;
import mx.uv.tcsw.ventas.infrastructure.persistence.ConfiguracionBaseDeDatos;
import mx.uv.tcsw.ventas.infrastructure.persistence.hibernate.HibernateProductoAdapter;
import mx.uv.tcsw.ventas.infrastructure.persistence.hibernate.HibernateSessionFactoryProvider;
import mx.uv.tcsw.ventas.infrastructure.persistence.hibernate.HibernateVentaAdapter;
import mx.uv.tcsw.ventas.infrastructure.persistence.jdbc.DatabaseConnectionManager;
import mx.uv.tcsw.ventas.infrastructure.persistence.jdbc.PostgresProductoAdapter;
import mx.uv.tcsw.ventas.infrastructure.persistence.jdbc.VentaJdbcRepository;

/**
 * Punto de ensamblaje de la API: aquí (y solo aquí) se elige el adaptador de persistencia,
 * igual que en MainSwing. El dominio y los casos de uso no conocen a Spring.
 * Variables de entorno: DB_URL, DB_USER, DB_PASSWORD y PERSISTENCIA (hibernate o jdbc).
 * No se carga con el perfil "test", que usan las pruebas HTTP con repositorios simulados.
 */
@Configuration
@Profile("!test")
public class ConfiguracionApi {

    @Bean
    public Persistencia persistencia() {
        String url = ConfiguracionBaseDeDatos.url();
        String usuario = ConfiguracionBaseDeDatos.usuario();
        String password = ConfiguracionBaseDeDatos.password();

        if ("jdbc".equals(ConfiguracionBaseDeDatos.modoPersistencia())) {
            DatabaseConnectionManager conexion = new DatabaseConnectionManager(url, usuario, password);
            return new Persistencia(new PostgresProductoAdapter(conexion), new VentaJdbcRepository(conexion), null);
        }
        SessionFactory sessionFactory = HibernateSessionFactoryProvider.crear(url, usuario, password);
        return new Persistencia(new HibernateProductoAdapter(sessionFactory),
                new HibernateVentaAdapter(sessionFactory), sessionFactory);
    }

    @Bean
    public ProductoRepository productoRepository(Persistencia persistencia) {
        return persistencia.productos();
    }

    @Bean
    public VentaRepository ventaRepository(Persistencia persistencia) {
        return persistencia.ventas();
    }

    @Bean
    public RegistrarVentaUseCase registrarVentaUseCase(VentaRepository ventaRepository) {
        return new RegistrarVentaService(ventaRepository);
    }

    @Bean
    public ConsultarProductosUseCase consultarProductosUseCase(ProductoRepository productoRepository) {
        return new ConsultarProductosService(productoRepository);
    }

    @Bean
    public VenderProductosUseCase venderProductosUseCase(ProductoRepository productoRepository,
            RegistrarVentaUseCase registrarVentaUseCase) {
        return new VenderProductosService(productoRepository, registrarVentaUseCase);
    }

    /** Agrupa los adaptadores elegidos y cierra la SessionFactory al apagar la aplicación. */
    public static final class Persistencia implements AutoCloseable {
        private final ProductoRepository productos;
        private final VentaRepository ventas;
        private final SessionFactory sessionFactory;

        Persistencia(ProductoRepository productos, VentaRepository ventas, SessionFactory sessionFactory) {
            this.productos = productos;
            this.ventas = ventas;
            this.sessionFactory = sessionFactory;
        }

        ProductoRepository productos() {
            return productos;
        }

        VentaRepository ventas() {
            return ventas;
        }

        @Override
        public void close() {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }
}
