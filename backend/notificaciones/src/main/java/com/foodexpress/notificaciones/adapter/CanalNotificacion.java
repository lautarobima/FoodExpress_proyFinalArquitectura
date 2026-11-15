package com.foodexpress.notificaciones.adapter;

import com.foodexpress.notificaciones.dto.MensajeNotificacion;
import com.foodexpress.notificaciones.model.enums.TipoCanal;

/**
 * Patrón Adapter (interfaz objetivo): todos los canales se usan igual,
 * aunque por debajo cada proveedor (correo, push, WebSocket) tenga una API
 * distinta. Agregar un canal nuevo (SMS, WhatsApp) es crear otro adaptador.
 */
public interface CanalNotificacion {

    TipoCanal tipo();

    void enviar(MensajeNotificacion mensaje);
}
