package com.foodexpress.pedidos.messaging.evento;

public record ItemEvento(
        Long platoId,
        String nombrePlato,
        Integer cantidad) {
}
