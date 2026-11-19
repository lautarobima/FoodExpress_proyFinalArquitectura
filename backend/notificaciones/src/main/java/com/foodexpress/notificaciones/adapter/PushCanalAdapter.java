package com.foodexpress.notificaciones.adapter;

import java.util.Map;

import com.foodexpress.notificaciones.adapter.externo.ServicioPushSimulado;
import com.foodexpress.notificaciones.dto.MensajeNotificacion;
import com.foodexpress.notificaciones.model.enums.TipoCanal;

import org.springframework.stereotype.Component;

// Adapta MensajeNotificacion a la API push(token, payload)
@Component
public class PushCanalAdapter implements CanalNotificacion {

    private final ServicioPushSimulado servicioPush;

    public PushCanalAdapter(ServicioPushSimulado servicioPush) {
        this.servicioPush = servicioPush;
    }

    @Override
    public TipoCanal tipo() {
        return TipoCanal.PUSH;
    }

    @Override
    public void enviar(MensajeNotificacion mensaje) {

        String token = "token-" + mensaje.tipoDestinatario().name().toLowerCase()
                + "-" + mensaje.destinatarioId();

        servicioPush.push(token, Map.of(
                "titulo", mensaje.titulo(),
                "cuerpo", mensaje.cuerpo(),
                "pedidoId", String.valueOf(mensaje.pedidoId())
        ));
    }
}
