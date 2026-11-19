package com.foodexpress.notificaciones.adapter.externo;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Simula un proveedor de notificaciones push (por ejemplo Firebase Cloud
 * Messaging), que trabaja con tokens de dispositivo y mapas de datos.
 */
@Component
public class ServicioPushSimulado {

    private static final Logger log =
            LoggerFactory.getLogger(ServicioPushSimulado.class);

    public void push(String deviceToken, Map<String, String> payload) {
        log.info("[PUSH] Dispositivo: {} | Datos: {}", deviceToken, payload);
    }
}
