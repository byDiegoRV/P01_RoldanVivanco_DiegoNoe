package mx.uv.tcsw.ventas.infrastructure.delivery.rest.dto;

import java.util.List;
import java.util.stream.Collectors;

import mx.uv.tcsw.ventas.domain.Venta;

public class VentaRespuesta {

    private final String folio;
    private final double total;
    private final List<PartidaRespuesta> partidas;

    public VentaRespuesta(String folio, double total, List<PartidaRespuesta> partidas) {
        this.folio = folio;
        this.total = total;
        this.partidas = partidas;
    }

    public static VentaRespuesta desde(Venta venta) {
        List<PartidaRespuesta> partidas = venta.getDetalles().stream()
                .map(PartidaRespuesta::desde)
                .collect(Collectors.toList());
        return new VentaRespuesta(venta.getFolio(), venta.calcularTotal(), partidas);
    }

    public String getFolio() {
        return folio;
    }

    public double getTotal() {
        return total;
    }

    public List<PartidaRespuesta> getPartidas() {
        return partidas;
    }
}
