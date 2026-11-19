package com.foodexpress.notificaciones.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.json.JsonMapper;

/**
 * Topología de RabbitMQ para Notificaciones:
 *
 * foodexpress.pedidos (topic)
 *   pedido.creado          -> notificaciones.pedido.creado
 *   pedido.estado.cambiado -> notificaciones.pedido.estado
 *   pedido.cancelado       -> notificaciones.pedido.cancelado
 *
 * Los mensajes que fallan después de los reintentos se envían a
 * foodexpress.dlx -> notificaciones.dlq para revisarlos después.
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "foodexpress.pedidos";
    public static final String DLX = "foodexpress.dlx";

    public static final String COLA_CREADO = "notificaciones.pedido.creado";
    public static final String COLA_ESTADO = "notificaciones.pedido.estado";
    public static final String COLA_CANCELADO = "notificaciones.pedido.cancelado";
    public static final String COLA_DLQ = "notificaciones.dlq";

    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX, true, false);
    }

    @Bean
    public Queue colaPedidoCreado() {
        return colaConDlq(COLA_CREADO);
    }

    @Bean
    public Queue colaEstadoCambiado() {
        return colaConDlq(COLA_ESTADO);
    }

    @Bean
    public Queue colaPedidoCancelado() {
        return colaConDlq(COLA_CANCELADO);
    }

    @Bean
    public Queue colaDlq() {
        return QueueBuilder.durable(COLA_DLQ).build();
    }

    @Bean
    public Binding bindingCreado() {
        return BindingBuilder.bind(colaPedidoCreado())
                .to(pedidosExchange()).with("pedido.creado");
    }

    @Bean
    public Binding bindingEstado() {
        return BindingBuilder.bind(colaEstadoCambiado())
                .to(pedidosExchange()).with("pedido.estado.cambiado");
    }

    @Bean
    public Binding bindingCancelado() {
        return BindingBuilder.bind(colaPedidoCancelado())
                .to(pedidosExchange()).with("pedido.cancelado");
    }

    @Bean
    public Binding bindingDlq() {
        return BindingBuilder.bind(colaDlq())
                .to(deadLetterExchange()).with(COLA_DLQ);
    }

    // El tipo del mensaje se deduce del parámetro del @RabbitListener,
    // así no se depende de los nombres de clase del microservicio Pedidos
    @Bean
    public MessageConverter messageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }

    private Queue colaConDlq(String nombre) {
        return QueueBuilder.durable(nombre)
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey(COLA_DLQ)
                .build();
    }
}
