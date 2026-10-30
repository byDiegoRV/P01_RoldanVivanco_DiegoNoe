package mx.uv.tcsw.ventas.infrastructure.persistence;

/** Configuración de conexión leída del entorno. No contiene credenciales en el código. */
public final class ConfiguracionBaseDeDatos {

    private static final String URL_POR_DEFECTO = "jdbc:postgresql://localhost:5435/ventas_test";
    private static final String USUARIO_POR_DEFECTO = "postgres";

    private ConfiguracionBaseDeDatos() {
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
            throw new IllegalStateException("Define la variable de entorno DB_PASSWORD para conectar con la base de datos");
        }
        return password;
    }

    /** "hibernate" (predeterminado) o "jdbc". */
    public static String modoPersistencia() {
        return System.getenv().getOrDefault("PERSISTENCIA", "hibernate").toLowerCase(java.util.Locale.ROOT);
    }
}
