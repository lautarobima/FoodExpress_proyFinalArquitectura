package com.foodexpress.pedidos.patterns.state;

import com.foodexpress.pedidos.exception.TransicionEstadoInvalidaException;
import com.foodexpress.pedidos.model.enums.EstadoPedido;

public class ListoState implements EstadoPedidoState {

    @Override
    public EstadoPedido getEstado() {
        return EstadoPedido.LISTO;
    }

    @Override
    public EstadoPedidoState avanzar() {
        return new EntregadoState();
    }

    // Un pedido listo ya fue preparado: no se puede cancelar
    @Override
    public EstadoPedidoState cancelar() {
        throw new TransicionEstadoInvalidaException(
                "Un pedido LISTO no se puede cancelar");
    }
}
