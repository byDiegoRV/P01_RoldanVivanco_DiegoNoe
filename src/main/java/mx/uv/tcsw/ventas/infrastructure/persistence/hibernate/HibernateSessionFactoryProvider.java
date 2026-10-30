package mx.uv.tcsw.ventas.infrastructure.persistence.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/** Construye la SessionFactory leyendo hibernate.cfg.xml; las credenciales llegan por parámetro. */
public final class HibernateSessionFactoryProvider {

    private HibernateSessionFactoryProvider() {
    }

    public static SessionFactory crear(String url, String usuario, String password) {
        Configuration configuracion = new Configuration().configure("hibernate.cfg.xml");
        configuracion.setProperty("hibernate.connection.url", url);
        configuracion.setProperty("hibernate.connection.username", usuario);
        configuracion.setProperty("hibernate.connection.password", password);
        return configuracion.buildSessionFactory();
    }
}
