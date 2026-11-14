
package com.foodexpress.restaurantes_menu.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Recurso no encontrado: HTTP 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(
            ResourceNotFoundException ex) {

        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    // Conflicto con reglas del negocio: HTTP 409
    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<Map<String, Object>> manejarConflicto(
            BusinessConflictException ex) {

        return construirRespuesta(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    // Errores de validación de DTO: HTTP 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());
        respuesta.put("error", "Error de validación");
        respuesta.put("detalles", errores);
        respuesta.put("timestamp", LocalDateTime.now());

        return ResponseEntity.badRequest().body(respuesta);
    }

    // Construir una respuesta de error uniforme
    private ResponseEntity<Map<String, Object>> construirRespuesta(
            HttpStatus status,
            String mensaje) {

        Map<String, Object> respuesta = new HashMap<>();

        respuesta.put("status", status.value());
        respuesta.put("error", status.getReasonPhrase());
        respuesta.put("message", mensaje);
        respuesta.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(status).body(respuesta);
    }
}
