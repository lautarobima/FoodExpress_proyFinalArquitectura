package com.foodexpress.notificaciones.messaging.evento;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoCanceladoEvento(
        Long pedidoId,
        Long clienteId,
        Long restauranteId,
        String estadoAnterior,
        String canceladoPor,
        String motivo,
        List<ItemEvento> items,
        LocalDateTime fecha) {
}
