package com.foodexpress.pedidos.patterns.state;

import com.foodexpress.pedidos.model.enums.EstadoPedido;

public class RecibidoState implements EstadoPedidoState {

    @Override
    public EstadoPedido getEstado() {
        return EstadoPedido.RECIBIDO;
    }

    @Override
    public EstadoPedidoState avanzar() {
        return new EnPreparacionState();
    }

    @Override
    public EstadoPedidoState cancelar() {
        return new CanceladoState();
    }
}
