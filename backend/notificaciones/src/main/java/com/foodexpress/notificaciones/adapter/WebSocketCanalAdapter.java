package com.foodexpress.notificaciones.adapter;

import com.foodexpress.notificaciones.dto.MensajeNotificacion;
import com.foodexpress.notificaciones.model.enums.TipoCanal;
import com.foodexpress.notificaciones.model.enums.TipoDestinatario;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Adapta MensajeNotificacion a SimpMessagingTemplate (STOMP sobre WebSocket).
 * Cliente:     /topic/clientes/{clienteId}
 * Restaurante: /topic/restaurantes/{restauranteId}  (panel de cocina)
 */
@Component
public class WebSocketCanalAdapter implements CanalNotificacion {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketCanalAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public TipoCanal tipo() {
        return TipoCanal.WEBSOCKET;
    }

    @Override
    public void enviar(MensajeNotificacion mensaje) {

        String destino = mensaje.tipoDestinatario() == TipoDestinatario.CLIENTE
                ? "/topic/clientes/" + mensaje.destinatarioId()
                : "/topic/restaurantes/" + mensaje.destinatarioId();

        messagingTemplate.convertAndSend(destino, mensaje);
    }
}
