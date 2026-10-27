package mx.uv.tcsw.ventas.infrastructure.persistence.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import mx.uv.tcsw.ventas.application.ports.out.VentaRepository;
import mx.uv.tcsw.ventas.domain.DetalleVenta;
import mx.uv.tcsw.ventas.domain.Venta;

public class VentaJdbcRepository implements VentaRepository {

    private static final String SQL_INSERT_VENTA =
        "INSERT INTO ventas (folio, total) VALUES (?, ?)";
    private static final String SQL_INSERT_DETALLE =
        "INSERT INTO detalle_venta (venta_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
    private static final String SQL_DESCONTAR_STOCK =
        "UPDATE productos SET stock = stock - ? WHERE id = ? AND stock >= ?";

    private final DatabaseConnectionManager connectionManager;

    public VentaJdbcRepository(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /** Registra la venta como una sola unidad de trabajo: se confirma completa o no se aplica ningún cambio. */
    @Override
public void guardar(Venta venta) {
    try (Connection cn = connectionManager.getConnection()) {
        cn.setAutoCommit(false);
        ejecutarEnTransaccion(cn, venta);
    } catch (SQLException ex) {
        throw new IllegalStateException("Error de conexión al registrar la venta", ex);
    }
}

private void ejecutarEnTransaccion(Connection cn, Venta venta) {
    try {
        int ventaId = insertarVenta(cn, venta);
        for (DetalleVenta detalle : venta.getDetalles()) {
            insertarDetalle(cn, ventaId, detalle);
            descontarExistencia(cn, detalle);
        }
        cn.commit();
    } catch (SQLException | RuntimeException ex) {
        revertir(cn, ex);
        throw new IllegalStateException("No se pudo registrar la venta " + venta.getFolio(), ex);
    }
}

    private int insertarVenta(Connection cn, Venta venta) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(SQL_INSERT_VENTA, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, venta.getFolio());
            ps.setBigDecimal(2, BigDecimal.valueOf(venta.calcularTotal()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("La base de datos no devolvió el id de la venta");
            }
        }
    }

    private void insertarDetalle(Connection cn, int ventaId, DetalleVenta detalle) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(SQL_INSERT_DETALLE)) {
            ps.setInt(1, ventaId);
            ps.setInt(2, idProducto(detalle));
            ps.setInt(3, detalle.getCantidad());
            ps.setBigDecimal(4, BigDecimal.valueOf(detalle.getPrecioUnitario()));
            ps.executeUpdate();
        }
    }

    private void descontarExistencia(Connection cn, DetalleVenta detalle) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(SQL_DESCONTAR_STOCK)) {
            ps.setInt(1, detalle.getCantidad());
            ps.setInt(2, idProducto(detalle));
            ps.setInt(3, detalle.getCantidad());
            if (ps.executeUpdate() != 1) {
                throw new IllegalStateException("Existencia insuficiente en base de datos para el producto "
                        + detalle.getProducto().getCodigo());
            }
        }
    }

    private int idProducto(DetalleVenta detalle) {
        Integer id = detalle.getProducto().getId();
        if (id == null) {
            throw new IllegalStateException("El producto " + detalle.getProducto().getCodigo()
                    + " no está persistido (sin id)");
        }
        return id;
    }

    private void revertir(Connection cn, Exception causa) {
        try {
            cn.rollback();
        } catch (SQLException rollbackEx) {
            causa.addSuppressed(rollbackEx); // no se pierde ni la causa ni el fallo del rollback
        }
    }
}