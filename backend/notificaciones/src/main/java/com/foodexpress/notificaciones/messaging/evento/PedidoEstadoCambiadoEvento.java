package com.foodexpress.notificaciones.messaging.evento;

import java.time.LocalDateTime;

public record PedidoEstadoCambiadoEvento(
        Long pedidoId,
        Long clienteId,
        Long restauranteId,
        String estadoAnterior,
        String estadoNuevo,
        LocalDateTime fecha) {
}
