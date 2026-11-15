package com.foodexpress.pedidos.exception;

// Regla de negocio incumplida al crear o modificar un pedido: HTTP 422
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
