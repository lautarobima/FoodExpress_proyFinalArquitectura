package com.foodexpress.restaurantes_menu.messaging.evento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Evento publicado en RabbitMQ con routing key pedido.creado
public record PedidoCreadoEvento(
        Long pedidoId,
        Long clienteId,
        Long restauranteId,
        BigDecimal total,
        List<ItemEvento> items,
        LocalDateTime fecha) {
}
