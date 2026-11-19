package com.foodexpress.notificaciones.messaging;

import com.foodexpress.notificaciones.messaging.evento.PedidoCanceladoEvento;
import com.foodexpress.notificaciones.messaging.evento.PedidoCreadoEvento;
import com.foodexpress.notificaciones.messaging.evento.PedidoEstadoCambiadoEvento;
import com.foodexpress.notificaciones.service.NotificacionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

// Consumidores de los eventos del microservicio Pedidos
@Component
public class PedidoEventosListener {

    private static final Logger log =
            LoggerFactory.getLogger(PedidoEventosListener.class);

    private final NotificacionService notificacionService;

    public PedidoEventosListener(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @RabbitListener(queues = RabbitConfig.COLA_CREADO)
    public void onPedidoCreado(PedidoCreadoEvento evento) {
        log.info("Recibido pedido.creado: pedido {}", evento.pedidoId());
        notificacionService.notificarPedidoCreado(evento);
    }

    @RabbitListener(queues = RabbitConfig.COLA_ESTADO)
    public void onEstadoCambiado(PedidoEstadoCambiadoEvento evento) {
        log.info("Recibido pedido.estado.cambiado: pedido {} {} -> {}",
                evento.pedidoId(), evento.estadoAnterior(), evento.estadoNuevo());
        notificacionService.notificarCambioEstado(evento);
    }

    @RabbitListener(queues = RabbitConfig.COLA_CANCELADO)
    public void onPedidoCancelado(PedidoCanceladoEvento evento) {
        log.info("Recibido pedido.cancelado: pedido {} por {}",
                evento.pedidoId(), evento.canceladoPor());
        notificacionService.notificarCancelacion(evento);
    }
}
