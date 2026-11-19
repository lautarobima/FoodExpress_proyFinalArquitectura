package com.foodexpress.pedidos.patterns.state;

import com.foodexpress.pedidos.model.enums.EstadoPedido;

// Obtiene el objeto State correspondiente al estado guardado en la base de datos
public final class EstadoPedidoStateFactory {

    private EstadoPedidoStateFactory() {
    }

    public static EstadoPedidoState desde(EstadoPedido estado) {
        return switch (estado) {
            case RECIBIDO -> new RecibidoState();
            case EN_PREPARACION -> new EnPreparacionState();
            case LISTO -> new ListoState();
            case ENTREGADO -> new EntregadoState();
            case CANCELADO -> new CanceladoState();
        };
    }
}
