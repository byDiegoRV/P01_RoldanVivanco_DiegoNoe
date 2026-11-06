package mx.uv.tcsw.ventas.infrastructure.delivery.rest.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/** Partida de la petición de venta. No incluye datos de tarjeta ni del cliente. */
public class PartidaPeticion {

    @NotNull(message = "es obligatorio")
    @Min(value = 1, message = "debe ser mayor o igual a 1")
    private Integer productoId;

    @NotNull(message = "es obligatoria")
    @Min(value = 1, message = "debe ser mayor o igual a 1")
    private Integer cantidad;

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
