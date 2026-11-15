package com.foodexpress.notificaciones.dto;

import java.time.LocalDateTime;

import com.foodexpress.notificaciones.model.entity.Notificacion;

public record NotificacionResponseDTO(
        Long id,
        Long pedidoId,
        String tipoEvento,
        String tipoDestinatario,
        Long destinatarioId,
        String canal,
        String titulo,
        String mensaje,
        String estado,
        String detalleError,
        LocalDateTime fechaEnvio) {

    public static NotificacionResponseDTO desde(Notificacion n) {
        return new NotificacionResponseDTO(
                n.getId(),
                n.getPedidoId(),
                n.getTipoEvento(),
                n.getTipoDestinatario().name(),
                n.getDestinatarioId(),
                n.getCanal().name(),
                n.getTitulo(),
                n.getMensaje(),
                n.getEstado().name(),
                n.getDetalleError(),
                n.getFechaEnvio());
    }
}
