package com.foodexpress.restaurantes_menu.messaging;

import com.foodexpress.restaurantes_menu.messaging.evento.PedidoCanceladoEvento;
import com.foodexpress.restaurantes_menu.messaging.evento.PedidoCreadoEvento;
import com.foodexpress.restaurantes_menu.service.PlatoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoEventosListener {
    private final PlatoService platoService;
    public PedidoEventosListener(PlatoService platoService) { this.platoService = platoService; }

    @RabbitListener(queues = RabbitConfig.Q_CREADO)
    public void onCreado(PedidoCreadoEvento e) {
        e.items().forEach(i -> platoService.ajustarStock(i.platoId(), -i.cantidad()));
    }

    @RabbitListener(queues = RabbitConfig.Q_CANCELADO)
    public void onCancelado(PedidoCanceladoEvento e) {
        e.items().forEach(i -> platoService.ajustarStock(i.platoId(), i.cantidad()));
    }
}
