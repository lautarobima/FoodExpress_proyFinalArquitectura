package com.foodexpress.notificaciones.dto;

import com.foodexpress.notificaciones.model.enums.TipoDestinatario;

// Mensaje común que reciben todos los canales (interfaz del Adapter)
public record MensajeNotificacion(
        Long pedidoId,
        String tipoEvento,
        TipoDestinatario tipoDestinatario,
        Long destinatarioId,
        String titulo,
        String cuerpo) {
}
