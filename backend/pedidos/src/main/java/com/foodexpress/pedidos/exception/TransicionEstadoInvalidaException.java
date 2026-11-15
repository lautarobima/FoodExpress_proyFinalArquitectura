package com.foodexpress.pedidos.exception;

// Transición de estado no permitida por la máquina de estados: HTTP 409
public class TransicionEstadoInvalidaException extends RuntimeException {

    public TransicionEstadoInvalidaException(String mensaje) {
        super(mensaje);
    }
}
