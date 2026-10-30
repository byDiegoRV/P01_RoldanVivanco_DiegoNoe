package mx.uv.tcsw.ventas.infrastructure.persistence.hibernate;

import java.math.BigDecimal;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import mx.uv.tcsw.ventas.application.ports.out.VentaRepository;
import mx.uv.tcsw.ventas.domain.DetalleVenta;
import mx.uv.tcsw.ventas.domain.Venta;
import mx.uv.tcsw.ventas.infrastructure.persistence.hibernate.entidad.DetalleVentaEntity;
import mx.uv.tcsw.ventas.infrastructure.persistence.hibernate.entidad.ProductoEntity;
import mx.uv.tcsw.ventas.infrastructure.persistence.hibernate.entidad.VentaEntity;

public class HibernateVentaAdapter implements VentaRepository {

    private static final String HQL_DESCONTAR_STOCK =
        "update ProductoEntity p set p.stock = p.stock - :cantidad where p.id = :id and p.stock >= :cantidad";

    private final SessionFactory sessionFactory;

    public HibernateVentaAdapter(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /** Unidad de negocio "registrar venta": una sola Session y una sola transacción. */
   @Override
   public void guardar(Venta venta) {
    Session session = sessionFactory.openSession();
    Transaction tx = null;
      try {
         tx = session.beginTransaction();

         VentaEntity ventaEntidad = new VentaEntity(venta.getFolio(), BigDecimal.valueOf(venta.calcularTotal()));
         session.persist(ventaEntidad);
         session.flush(); // el INSERT del encabezado se envía ya, dentro de la transacción

         for (DetalleVenta detalle : venta.getDetalles()) {
            registrarDetalle(session, ventaEntidad, detalle);
         }
         tx.commit();
    }    catch (RuntimeException ex) {
         revertir(tx, ex); // el rollback ocurre ANTES de cerrar la sesión
         throw new IllegalStateException("No se pudo registrar la venta " + venta.getFolio(), ex);
    } finally {
        session.close();
       }
   }  

    private void registrarDetalle(Session session, VentaEntity venta, DetalleVenta detalle) {
        Integer idProducto = detalle.getProducto().getId();
        if (idProducto == null) {
            throw new IllegalStateException("El producto " + detalle.getProducto().getCodigo()
                    + " no está persistido (sin id)");
        }
        ProductoEntity producto = session.getReference(ProductoEntity.class, idProducto);
        session.persist(new DetalleVentaEntity(venta, producto, detalle.getCantidad(),
                BigDecimal.valueOf(detalle.getPrecioUnitario())));
        session.flush(); // aquí la base valida la clave foránea

        int filas = session.createMutationQuery(HQL_DESCONTAR_STOCK)
                .setParameter("cantidad", detalle.getCantidad())
                .setParameter("id", idProducto)
                .executeUpdate();
        if (filas != 1) {
            throw new IllegalStateException("Existencia insuficiente en base de datos para el producto "
                    + detalle.getProducto().getCodigo());
        }
    }

    private void revertir(Transaction tx, RuntimeException causa) {
        if (tx != null && tx.isActive()) {
            try {
                tx.rollback();
            } catch (RuntimeException rollbackEx) {
                causa.addSuppressed(rollbackEx); // no se pierde ni la causa ni el fallo del rollback
            }
        }
    }
}
