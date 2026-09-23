package mx.uv.tcsw.ventas.application.service;

public class PagoFactory {
    
    // Constructor privado para ocultar el público implícito
    private PagoFactory() {
        throw new UnsupportedOperationException("Clase utilitaria de fábrica");
    }

    public static MetodoPago obtenerMetodoPago(String tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de pago no puede ser nulo");
        }
        
        // Reemplazamos switch por if-else para cumplir con la métrica de SonarQube
        if ("EFECTIVO".equalsIgnoreCase(tipo)) {
            return new PagoEfectivo();
        }
        
        throw new IllegalArgumentException("Método de pago desconocido: " + tipo);
    }
}