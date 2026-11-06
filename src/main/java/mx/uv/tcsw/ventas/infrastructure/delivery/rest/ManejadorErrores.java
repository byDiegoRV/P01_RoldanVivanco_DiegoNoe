package mx.uv.tcsw.ventas.infrastructure.delivery.rest;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import mx.uv.tcsw.ventas.application.exception.ProductoNoEncontradoException;
import mx.uv.tcsw.ventas.domain.ExistenciaInsuficienteException;
import mx.uv.tcsw.ventas.infrastructure.delivery.rest.dto.ErrorRespuesta;

/**
 * Traduce excepciones a respuestas con un único formato ({@link ErrorRespuesta}).
 * Los detalles técnicos se registran en el servidor y nunca se devuelven al cliente.
 */
@RestControllerAdvice
public class ManejadorErrores extends ResponseEntityExceptionHandler {

    static final String MENSAJE_INTERNO = "Ocurrió un error interno. Intenta de nuevo más tarde.";

    private static final Logger REGISTRO = LoggerFactory.getLogger(ManejadorErrores.class);
    static final String CODIGO_SOLICITUD_INVALIDA = "SOLICITUD_INVALIDA";

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<Object> productoNoEncontrado(ProductoNoEncontradoException ex, WebRequest request) {
        return responder(HttpStatus.NOT_FOUND, "PRODUCTO_NO_ENCONTRADO", ex.getMessage(), Collections.emptyList(),
                new HttpHeaders(), request);
    }

    @ExceptionHandler(ExistenciaInsuficienteException.class)
    public ResponseEntity<Object> existenciaInsuficiente(ExistenciaInsuficienteException ex, WebRequest request) {
        String mensaje = ex.getProductoId() == null
                ? "No hay existencia suficiente para completar la venta."
                : "No hay existencia suficiente para el producto con id " + ex.getProductoId() + ".";
        return responder(HttpStatus.CONFLICT, "EXISTENCIA_INSUFICIENTE", mensaje, Collections.emptyList(),
                new HttpHeaders(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> errorInterno(Exception ex, WebRequest request) {
        REGISTRO.error("Error interno no controlado", ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO", MENSAJE_INTERNO,
                Collections.emptyList(), new HttpHeaders(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.toList());
        return responder(HttpStatus.BAD_REQUEST, "VALIDACION_FALLIDA", "La petición contiene datos inválidos.",
                detalles, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        return responder(HttpStatus.BAD_REQUEST, CODIGO_SOLICITUD_INVALIDA,
                "El cuerpo de la petición no es válido: JSON mal formado o con campos no permitidos.",
                Collections.emptyList(), headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        String parametro = ex instanceof MethodArgumentTypeMismatchException
                ? ((MethodArgumentTypeMismatchException) ex).getName()
                : ex.getPropertyName();
        return responder(HttpStatus.BAD_REQUEST, CODIGO_SOLICITUD_INVALIDA,
                "El parámetro '" + parametro + "' debe ser un número entero.", Collections.emptyList(), headers,
                request);
    }

    /** Resto de errores estándar de Spring MVC (ruta inexistente, método o tipo no soportado, etc.). */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        if (status.is5xxServerError()) {
            REGISTRO.error("Error interno de Spring MVC", ex);
        }
        return responder(status, codigoPara(status), mensajePara(status), Collections.emptyList(), headers,
                request);
    }

    private static String codigoPara(HttpStatus status) {
        if (status.is5xxServerError()) {
            return "ERROR_INTERNO";
        }
        switch (status) {
            case NOT_FOUND:
                return "RECURSO_NO_ENCONTRADO";
            case METHOD_NOT_ALLOWED:
                return "METODO_NO_PERMITIDO";
            case UNSUPPORTED_MEDIA_TYPE:
                return "TIPO_NO_SOPORTADO";
            default:
                return CODIGO_SOLICITUD_INVALIDA;
        }
    }

    private static String mensajePara(HttpStatus status) {
        if (status.is5xxServerError()) {
            return MENSAJE_INTERNO;
        }
        switch (status) {
            case NOT_FOUND:
                return "El recurso solicitado no existe.";
            case METHOD_NOT_ALLOWED:
                return "El método HTTP no está permitido para este recurso.";
            case UNSUPPORTED_MEDIA_TYPE:
                return "El tipo de contenido no está soportado; usa application/json.";
            default:
                return "La solicitud no es válida.";
        }
    }

    private ResponseEntity<Object> responder(HttpStatus status, String codigo, String mensaje,
            List<String> detalles, HttpHeaders headers, WebRequest request) {
        String ruta = request instanceof ServletWebRequest
                ? ((ServletWebRequest) request).getRequest().getRequestURI()
                : "";
        ErrorRespuesta cuerpo = new ErrorRespuesta(Instant.now().toString(), status.value(), codigo, mensaje, ruta,
                detalles);
        return ResponseEntity.status(status)
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .body(cuerpo);
    }
}
