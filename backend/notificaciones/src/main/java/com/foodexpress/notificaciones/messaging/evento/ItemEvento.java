package com.foodexpress.notificaciones.messaging.evento;

public record ItemEvento(
        Long platoId,
        String nombrePlato,
        Integer cantidad) {
}
