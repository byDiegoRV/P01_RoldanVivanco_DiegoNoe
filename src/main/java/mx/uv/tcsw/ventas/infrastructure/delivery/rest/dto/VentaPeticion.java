package mx.uv.tcsw.ventas.infrastructure.delivery.rest.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/** Cuerpo de POST /api/ventas. El folio lo genera el servidor. */
public class VentaPeticion {

    @NotEmpty(message = "debe incluir al menos una partida")
    @Size(max = 50, message = "no puede tener más de 50 partidas")
    @Valid
    private List<PartidaPeticion> partidas;

    public List<PartidaPeticion> getPartidas() {
        return partidas;
    }

    public void setPartidas(List<PartidaPeticion> partidas) {
        this.partidas = partidas;
    }
}
