package com.foodexpress.restaurantes_menu.messaging.evento;

public record ItemEvento(
        Long platoId,
        String nombrePlato,
        Integer cantidad) {
}
