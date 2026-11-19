package com.foodexpress.pedidos.patterns.state;

import com.foodexpress.pedidos.model.enums.EstadoPedido;

/**
 * Patrón State: cada estado del pedido decide cuáles transiciones son válidas.
 * Agregar un estado nuevo (por ejemplo EnCamino para delivery) implica crear
 * una clase nueva sin modificar la lógica de los demás estados (OCP).
 */
public interface EstadoPedidoState {

    EstadoPedido getEstado();

    // Pasar al siguiente estado del flujo normal
    EstadoPedidoState avanzar();

    // Cancelar el pedido
    EstadoPedidoState cancelar();

    default boolean esFinal() {
        return false;
    }
}
