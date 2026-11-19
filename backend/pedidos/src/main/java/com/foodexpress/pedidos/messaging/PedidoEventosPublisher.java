package com.foodexpress.pedidos.messaging;

import com.foodexpress.pedidos.messaging.evento.PedidoCanceladoEvento;
import com.foodexpress.pedidos.messaging.evento.PedidoCreadoEvento;
import com.foodexpress.pedidos.messaging.evento.PedidoEstadoCambiadoEvento;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Envía los eventos a RabbitMQ solo después de que la transacción hizo commit,
 * para no anunciar pedidos que terminaron en rollback.
 * Si RabbitMQ no responde, el pedido igual queda guardado (disponibilidad):
 * el error se registra en el log y la operación del cliente no falla.
 */
@Component
public class PedidoEventosPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(PedidoEventosPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public PedidoEventosPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPedidoCreado(PedidoCreadoEvento evento) {
        publicar(RabbitConfig.RK_PEDIDO_CREADO, evento, evento.pedidoId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEstadoCambiado(PedidoEstadoCambiadoEvento evento) {
        publicar(RabbitConfig.RK_ESTADO_CAMBIADO, evento, evento.pedidoId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPedidoCancelado(PedidoCanceladoEvento evento) {
        publicar(RabbitConfig.RK_PEDIDO_CANCELADO, evento, evento.pedidoId());
    }

    private void publicar(String routingKey, Object evento, Long pedidoId) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE, routingKey, evento);
            log.info("Evento {} publicado para el pedido {}", routingKey, pedidoId);

        } catch (AmqpException ex) {
            log.error("No se pudo publicar {} del pedido {}: {}",
                    routingKey, pedidoId, ex.getMessage());
        }
    }
}
