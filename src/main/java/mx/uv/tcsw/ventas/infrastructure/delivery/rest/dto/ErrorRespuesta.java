package mx.uv.tcsw.ventas.infrastructure.delivery.rest.dto;

import java.util.List;

/** Formato único de error de la API. Nunca contiene trazas, SQL ni credenciales. */
public class ErrorRespuesta {

    private final String timestamp;
    private final int status;
    private final String code;
    private final String message;
    private final String path;
    private final List<String> details;

    public ErrorRespuesta(String timestamp, int status, String code, String message, String path,
            List<String> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.code = code;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<String> getDetails() {
        return details;
    }
}
