package com.foodexpress.restaurantes_menu.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "foodexpress.pedidos";
    public static final String Q_CREADO = "restaurantes.pedido.creado";
    public static final String Q_CANCELADO = "restaurantes.pedido.cancelado";

    @Bean
    TopicExchange pedidosExchange() { return new TopicExchange(EXCHANGE, true, false); }
    @Bean
    Queue qCreado() { return new Queue(Q_CREADO, true); }
    @Bean
    Queue qCancelado() { return new Queue(Q_CANCELADO, true); }
    @Bean
    Binding bCreado(Queue qCreado, TopicExchange ex) {
        return BindingBuilder.bind(qCreado).to(ex).with("pedido.creado"); }
    @Bean Binding bCancelado(Queue qCancelado, TopicExchange ex) {
        return BindingBuilder.bind(qCancelado).to(ex).with("pedido.cancelado"); }

    @Bean
    MessageConverter messageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}
