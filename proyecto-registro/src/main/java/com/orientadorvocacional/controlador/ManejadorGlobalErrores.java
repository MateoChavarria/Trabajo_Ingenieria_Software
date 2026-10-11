package com.orientadorvocacional.controlador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Traduce errores inesperados a respuestas HTTP con mensajes genericos,
 * para no exponer trazas, nombres de clases ni detalles de la base de
 * datos al cliente. El detalle completo queda en el log del servidor.
 */
@RestControllerAdvice
public class ManejadorGlobalErrores {

    private static final Logger LOG = LoggerFactory.getLogger(ManejadorGlobalErrores.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> cuerpoInvalido(HttpMessageNotReadableException excepcion) {
        return ResponseEntity.badRequest().body("La petición no tiene un formato válido.");
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ServletRequestBindingException.class})
    public ResponseEntity<String> parametroInvalido(Exception excepcion) {
        return ResponseEntity.badRequest().body("Parámetros de la petición no válidos.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> datosInvalidos(IllegalArgumentException excepcion) {
        return ResponseEntity.badRequest().body(excepcion.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> errorInesperado(Exception excepcion) {
        if (excepcion instanceof ErrorResponse respuestaSpring) {
            return ResponseEntity.status(respuestaSpring.getStatusCode()).body("Solicitud no válida.");
        }
        LOG.error("Error inesperado procesando la petición", excepcion);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error inesperado. Intenta más tarde.");
    }
}