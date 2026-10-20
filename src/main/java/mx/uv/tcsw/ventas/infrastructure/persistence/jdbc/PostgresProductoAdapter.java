package mx.uv.tcsw.ventas.infrastructure.persistence.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.uv.tcsw.ventas.application.ports.out.ProductoRepository;
import mx.uv.tcsw.ventas.domain.Producto;

public class PostgresProductoAdapter implements ProductoRepository {

    private static final Logger LOGGER = Logger.getLogger(PostgresProductoAdapter.class.getName());
    private final DatabaseConnectionManager connectionManager;

    public PostgresProductoAdapter(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void guardar(Producto producto) {
        String sql = "INSERT INTO productos (codigo, nombre, precio, stock) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = connectionManager.getConnection()) {
            conn.setAutoCommit(false);
            ejecutarInsercion(conn, sql, producto);
            conn.commit();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener la conexión o gestionar la transacción con la base de datos", e);
        }
    }

    private void ejecutarInsercion(Connection conn, String sql, Producto producto) {
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setBigDecimal(3, BigDecimal.valueOf(producto.getPrecio()));
            stmt.setInt(4, producto.getExistencia());
            
            stmt.executeUpdate();
            recuperarIdGenerado(stmt, producto);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Error al realizar rollback en la transacción", rollbackEx);
            }
            throw new IllegalStateException("Error al ejecutar la inserción del producto en la base de datos", e);
        }
    }

    private void recuperarIdGenerado(PreparedStatement stmt, Producto producto) throws SQLException {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                producto.setId(generatedKeys.getInt(1));
            }
        }
    }

    @Override
    public Optional<Producto> buscarPorId(Integer id) {
        String sql = "SELECT id, codigo, nombre, precio, stock FROM productos WHERE id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Producto producto = new Producto(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                    );
                    return Optional.of(producto);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar el producto por ID en la base de datos", e);
        }
        
        return Optional.empty();
    }
}