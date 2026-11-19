package com.foodexpress.pedidos.messaging.evento;

import java.time.LocalDateTime;

// Evento publicado en RabbitMQ con routing key pedido.estado.cambiado
public record PedidoEstadoCambiadoEvento(
        Long pedidoId,
        Long clienteId,
        Long restauranteId,
        String estadoAnterior,
        String estadoNuevo,
        LocalDateTime fecha) {
}
