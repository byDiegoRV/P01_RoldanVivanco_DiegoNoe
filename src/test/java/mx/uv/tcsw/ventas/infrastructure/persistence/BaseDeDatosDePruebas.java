package mx.uv.tcsw.ventas.infrastructure.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import mx.uv.tcsw.ventas.infrastructure.persistence.jdbc.DatabaseConnectionManager;

/**
 * Configuración y fixture compartidos por las pruebas que usan PostgreSQL
 * (JDBC e Hibernate). Lee la conexión del entorno; no contiene credenciales.
 */
public final class BaseDeDatosDePruebas {

    private static final String URL_POR_DEFECTO = "jdbc:postgresql://localhost:5435/ventas_test";
    private static final String USUARIO_POR_DEFECTO = "postgres";
    private static final String SQL_LIMPIAR =
        "TRUNCATE TABLE detalle_venta, ventas, productos RESTART IDENTITY";

    private BaseDeDatosDePruebas() {
    }

    public static String url() {
        return System.getenv().getOrDefault("DB_URL", URL_POR_DEFECTO);
    }

    public static String usuario() {
        return System.getenv().getOrDefault("DB_USER", USUARIO_POR_DEFECTO);
    }

    public static String password() {
        String password = System.getenv("DB_PASSWORD");
        if (password == null) {
            throw new IllegalStateException(
                "Define la variable de entorno DB_PASSWORD antes de ejecutar las pruebas");
        }
        return password;
    }

    public static DatabaseConnectionManager crearConexion() {
        return new DatabaseConnectionManager(url(), usuario(), password());
    }

    /** Deja las tablas vacías y los contadores en 1: estado inicial conocido. */
    public static void limpiar(DatabaseConnectionManager connectionManager) throws SQLException {
        try (Connection cn = connectionManager.getConnection(); Statement st = cn.createStatement()) {
            st.execute(SQL_LIMPIAR);
        }
    }
}