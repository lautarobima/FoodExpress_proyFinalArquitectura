package com.foodexpress.pedidos.patterns.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.foodexpress.pedidos.exception.TransicionEstadoInvalidaException;
import com.foodexpress.pedidos.model.entity.Pedido;
import com.foodexpress.pedidos.model.enums.EstadoPedido;

import org.junit.jupiter.api.Test;

// Pruebas unitarias de la máquina de estados (no necesitan base de datos)
class EstadoPedidoStateTest {

    private Pedido pedidoEn(EstadoPedido estado) {
        Pedido pedido = new Pedido();
        pedido.setEstado(estado);
        return pedido;
    }

    @Test
    void flujoNormalLlegaAEntregado() {
        Pedido pedido = pedidoEn(EstadoPedido.RECIBIDO);

        pedido.avanzarEstado();
        assertEquals(EstadoPedido.EN_PREPARACION, pedido.getEstado());

        pedido.avanzarEstado();
        assertEquals(EstadoPedido.LISTO, pedido.getEstado());

        pedido.avanzarEstado();
        assertEquals(EstadoPedido.ENTREGADO, pedido.getEstado());
    }

    @Test
    void sePuedeCancelarRecibidoYEnPreparacion() {
        Pedido recibido = pedidoEn(EstadoPedido.RECIBIDO);
        recibido.cancelar("Cliente se arrepintió");
        assertEquals(EstadoPedido.CANCELADO, recibido.getEstado());
        assertEquals("Cliente se arrepintió", recibido.getMotivoCancelacion());

        Pedido enPreparacion = pedidoEn(EstadoPedido.EN_PREPARACION);
        enPreparacion.cancelar("Sin ingredientes");
        assertEquals(EstadoPedido.CANCELADO, enPreparacion.getEstado());
    }

    @Test
    void noSePuedeCancelarListoNiEntregado() {
        assertThrows(TransicionEstadoInvalidaException.class,
                () -> pedidoEn(EstadoPedido.LISTO).cancelar("x"));

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> pedidoEn(EstadoPedido.ENTREGADO).cancelar("x"));
    }

    @Test
    void estadosFinalesNoAvanzan() {
        assertThrows(TransicionEstadoInvalidaException.class,
                () -> pedidoEn(EstadoPedido.ENTREGADO).avanzarEstado());

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> pedidoEn(EstadoPedido.CANCELADO).avanzarEstado());

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> pedidoEn(EstadoPedido.CANCELADO).cancelar("x"));
    }

    @Test
    void transicionInvalidaNoCambiaElEstado() {
        Pedido pedido = pedidoEn(EstadoPedido.LISTO);

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> pedido.cancelar("x"));

        assertEquals(EstadoPedido.LISTO, pedido.getEstado());
    }
}
