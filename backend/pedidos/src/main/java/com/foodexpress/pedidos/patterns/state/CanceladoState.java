package com.foodexpress.pedidos.patterns.state;

import com.foodexpress.pedidos.exception.TransicionEstadoInvalidaException;
import com.foodexpress.pedidos.model.enums.EstadoPedido;

public class CanceladoState implements EstadoPedidoState {

    @Override
    public EstadoPedido getEstado() {
        return EstadoPedido.CANCELADO;
    }

    @Override
    public EstadoPedidoState avanzar() {
        throw new TransicionEstadoInvalidaException(
                "El pedido está cancelado");
    }

    @Override
    public EstadoPedidoState cancelar() {
        throw new TransicionEstadoInvalidaException(
                "El pedido ya está cancelado");
    }

    @Override
    public boolean esFinal() {
        return true;
    }
}
