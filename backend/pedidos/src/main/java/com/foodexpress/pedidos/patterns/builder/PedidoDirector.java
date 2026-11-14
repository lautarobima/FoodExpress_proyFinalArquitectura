package com.foodexpress.pedidos.patterns.builder;

import com.foodexpress.pedidos.model.entity.Pedido;
import com.foodexpress.pedidos.model.enums.EstadoPedido;

import java.time.LocalDateTime;

public class PedidoDirector {

    public Pedido construirPedidoBasico(
            Long clienteId,
            Long restauranteId) {

        PedidoBuilder builder = new PedidoBuilder();

        return builder
                .conCliente(clienteId)
                .conRestaurante(restauranteId)
                .conFechaCreacion(LocalDateTime.now())
                .conEstado(EstadoPedido.RECIBIDO)
                .build();
    }
}