package com.foodexpress.notificaciones.adapter.externo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Simula una librería externa de correo (por ejemplo SendGrid o JavaMail)
 * con una API propia que no coincide con CanalNotificacion.
 */
@Component
public class ServicioCorreoSimulado {

    private static final Logger log =
            LoggerFactory.getLogger(ServicioCorreoSimulado.class);

    public void sendMail(String to, String subject, String htmlBody) {
        log.info("[EMAIL] Para: {} | Asunto: {} | Cuerpo: {}", to, subject, htmlBody);
    }
}
