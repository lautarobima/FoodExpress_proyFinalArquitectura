package com.foodexpress.restaurantes_menu.messaging.evento;

import java.time.LocalDateTime;
import java.util.List;

// Evento publicado en RabbitMQ con routing key pedido.cancelado
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
