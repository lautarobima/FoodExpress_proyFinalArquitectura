
package com.foodexpress.pedidos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Pedido inexistente
    @ExceptionHandler(PedidoNoEncontradoException.class)
    public ResponseEntity<ApiErrorDTO> manejarPedidoNoEncontrado(
            PedidoNoEncontradoException ex) {

        ApiErrorDTO error = new ApiErrorDTO(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Transición de estado no permitida
    @ExceptionHandler(TransicionEstadoInvalidaException.class)
    public ResponseEntity<ApiErrorDTO> manejarTransicionInvalida(
            TransicionEstadoInvalidaException ex) {

        ApiErrorDTO error = new ApiErrorDTO(
                HttpStatus.CONFLICT.value(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // Regla de negocio incumplida (restaurante cerrado, inexistente, etc.)
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ApiErrorDTO> manejarReglaNegocio(
            ReglaNegocioException ex) {

        ApiErrorDTO error = new ApiErrorDTO(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    // Datos inválidos o reglas de negocio incumplidas
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDTO> manejarDatosInvalidos(
            IllegalArgumentException ex) {

        ApiErrorDTO error = new ApiErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(error);
    }

    // Errores de validación de los DTOs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> manejarValidaciones(
            MethodArgumentNotValidException ex) {

        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Los datos enviados no son válidos");

        ApiErrorDTO error = new ApiErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                mensaje
        );

        return ResponseEntity.badRequest().body(error);
    }

    // Problemas de comunicación con otro microservicio
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiErrorDTO> manejarErrorComunicacion(
            RestClientException ex) {

        ApiErrorDTO error = new ApiErrorDTO(
                HttpStatus.BAD_GATEWAY.value(),
                "No fue posible comunicarse con el microservicio Restaurantes y Menú"
        );

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }
}
