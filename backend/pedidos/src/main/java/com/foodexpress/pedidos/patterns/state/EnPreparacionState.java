package com.foodexpress.pedidos.patterns.state;

import com.foodexpress.pedidos.model.enums.EstadoPedido;

public class EnPreparacionState implements EstadoPedidoState {

    @Override
    public EstadoPedido getEstado() {
        return EstadoPedido.EN_PREPARACION;
    }

    @Override
    public EstadoPedidoState avanzar() {
        return new ListoState();
    }

    @Override
    public EstadoPedidoState cancelar() {
        return new CanceladoState();
    }
}
