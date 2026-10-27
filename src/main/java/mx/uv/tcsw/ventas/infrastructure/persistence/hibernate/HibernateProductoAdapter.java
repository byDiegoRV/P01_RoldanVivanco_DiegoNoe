package mx.uv.tcsw.ventas.infrastructure.persistence.hibernate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import mx.uv.tcsw.ventas.application.ports.out.ProductoRepository;
import mx.uv.tcsw.ventas.domain.Producto;
import mx.uv.tcsw.ventas.infrastructure.persistence.hibernate.entidad.ProductoEntity;

public class HibernateProductoAdapter implements ProductoRepository {

    private final SessionFactory sessionFactory;

    public HibernateProductoAdapter(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(Producto producto) {
        ProductoEntity entidad = new ProductoEntity(producto.getCodigo(), producto.getNombre(),
                BigDecimal.valueOf(producto.getPrecio()), producto.getExistencia());
        ejecutarEnTransaccion("guardar el producto", session -> session.persist(entidad));
        producto.setId(entidad.getId());
    }

    @Override
    public Optional<Producto> buscarPorId(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(ProductoEntity.class, id)).map(this::aDominio);
        }
    }

    @Override
    public Optional<Producto> buscarPorCodigo(String codigo) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from ProductoEntity p where p.codigo = :codigo", ProductoEntity.class)
                    .setParameter("codigo", codigo)
                    .uniqueResultOptional()
                    .map(this::aDominio);
        }
    }

    @Override
    public List<Producto> listarTodos() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from ProductoEntity p order by p.id", ProductoEntity.class)
                    .getResultList().stream().map(this::aDominio).collect(Collectors.toList());
        }
    }

    @Override
    public void actualizar(Producto producto) {
        ejecutarEnTransaccion("actualizar el producto", session -> {
            ProductoEntity entidad = session.get(ProductoEntity.class, producto.getId());
            if (entidad == null) {
                throw new IllegalStateException("El producto " + producto.getId() + " no existe");
            }
            entidad.setCodigo(producto.getCodigo());
            entidad.setNombre(producto.getNombre());
            entidad.setPrecio(BigDecimal.valueOf(producto.getPrecio()));
            entidad.setStock(producto.getExistencia());
        });
    }

    @Override
    public void eliminar(Integer id) {
        ejecutarEnTransaccion("eliminar el producto", session -> {
            ProductoEntity entidad = session.get(ProductoEntity.class, id);
            if (entidad != null) {
                session.remove(entidad);
            }
        });
    }

    private Producto aDominio(ProductoEntity e) {
        return new Producto(e.getId(), e.getCodigo(), e.getNombre(), e.getPrecio().doubleValue(), e.getStock());
    }

    private void ejecutarEnTransaccion(String accion, java.util.function.Consumer<Session> trabajo) {
    Session session = sessionFactory.openSession();
    Transaction tx = null;
    try {
        tx = session.beginTransaction();
        trabajo.accept(session);
        tx.commit();
    } catch (RuntimeException ex) {
        if (tx != null && tx.isActive()) {
            try {
                tx.rollback();
            } catch (RuntimeException rollbackEx) {
                ex.addSuppressed(rollbackEx);
            }
        }
        throw new IllegalStateException("No se pudo " + accion, ex);
    } finally {
        session.close();
    }
  }  
}
