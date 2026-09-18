package mx.uv.tcsw.ventas.adapter.out.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mx.uv.tcsw.ventas.application.ports.out.VentaRepository;
import mx.uv.tcsw.ventas.domain.Venta;

public class VentaRepositoryInMemory implements VentaRepository {

    private final List<Venta> baseDeDatosSimulada = new ArrayList<>();

    @Override
    public void guardar(Venta venta) {
        if (venta != null) {
            baseDeDatosSimulada.add(venta);
        }
    }

    public List<Venta> obtenerTodas() {
        return Collections.unmodifiableList(baseDeDatosSimulada);
    }
}