package com.foodexpress.pedidos.messaging;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "foodexpress.pedidos";

    public static final String RK_PEDIDO_CREADO = "pedido.creado";
    public static final String RK_ESTADO_CAMBIADO = "pedido.estado.cambiado";
    public static final String RK_PEDIDO_CANCELADO = "pedido.cancelado";

    // Pedidos solo declara el exchange: cada consumidor declara sus colas
    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    // Los eventos viajan como JSON
    @Bean
    public MessageConverter messageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}
