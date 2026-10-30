package mx.uv.tcsw.ventas.infrastructure.persistence.hibernate.entidad;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ventas")
public class VentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "folio", nullable = false, unique = true, length = 50)
    private String folio;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    protected VentaEntity() {
        // requerido por Hibernate
    }

    public VentaEntity(String folio, BigDecimal total) {
        this.folio = folio;
        this.total = total;
    }

    public Integer getId() {
        return id;
    }

    public String getFolio() {
        return folio;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
