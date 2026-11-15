package com.foodexpress.pedidos.patterns.state;

import com.foodexpress.pedidos.exception.TransicionEstadoInvalidaException;
import com.foodexpress.pedidos.model.enums.EstadoPedido;

public class EntregadoState implements EstadoPedidoState {

    @Override
    public EstadoPedido getEstado() {
        return EstadoPedido.ENTREGADO;
    }

    @Override
    public EstadoPedidoState avanzar() {
        throw new TransicionEstadoInvalidaException(
                "El pedido ya fue entregado");
    }

    @Override
    public EstadoPedidoState cancelar() {
        throw new TransicionEstadoInvalidaException(
                "Un pedido entregado no se puede cancelar");
    }

    @Override
    public boolean esFinal() {
        return true;
    }
}
