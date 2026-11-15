package com.foodexpress.notificaciones.adapter;

import com.foodexpress.notificaciones.adapter.externo.ServicioCorreoSimulado;
import com.foodexpress.notificaciones.dto.MensajeNotificacion;
import com.foodexpress.notificaciones.model.enums.TipoCanal;

import org.springframework.stereotype.Component;

// Adapta MensajeNotificacion a la API sendMail(to, subject, body)
@Component
public class EmailCanalAdapter implements CanalNotificacion {

    private final ServicioCorreoSimulado servicioCorreo;

    public EmailCanalAdapter(ServicioCorreoSimulado servicioCorreo) {
        this.servicioCorreo = servicioCorreo;
    }

    @Override
    public TipoCanal tipo() {
        return TipoCanal.EMAIL;
    }

    @Override
    public void enviar(MensajeNotificacion mensaje) {

        // Sin servicio de usuarios, la dirección se arma a partir del ID
        String destinatario = mensaje.tipoDestinatario().name().toLowerCase()
                + mensaje.destinatarioId() + "@foodexpress.test";

        String cuerpoHtml = "<p>" + mensaje.cuerpo() + "</p>";

        servicioCorreo.sendMail(destinatario, mensaje.titulo(), cuerpoHtml);
    }
}
