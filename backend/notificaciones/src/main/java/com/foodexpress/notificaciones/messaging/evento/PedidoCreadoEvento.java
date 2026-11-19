package com.foodexpress.notificaciones.messaging.evento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Copia local del contrato del evento pedido.creado (cada servicio tiene la suya)
public record PedidoCreadoEvento(
        Long pedidoId,
        Long clienteId,
        Long restauranteId,
        BigDecimal total,
        List<ItemEvento> items,
        LocalDateTime fecha) {
}
