package com.foodexpress.pedidos.patterns.builder;

import com.foodexpress.pedidos.model.entity.Pedido;
import com.foodexpress.pedidos.model.entity.ItemPedido;
import com.foodexpress.pedidos.model.entity.ItemPedidoExtra;
import com.foodexpress.pedidos.model.enums.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PedidoBuilder {

    private Pedido pedido;

    public PedidoBuilder() {
        this.pedido = new Pedido();
    }

    public PedidoBuilder conCliente(Long clienteId) {
        pedido.setClienteId(clienteId);
        return this;
    }

    public PedidoBuilder conRestaurante(Long restauranteId) {
        pedido.setRestauranteId(restauranteId);
        return this;
    }

    public PedidoBuilder conFechaCreacion(LocalDateTime fechaCreacion) {
        pedido.setFechaCreacion(fechaCreacion);
        return this;
    }

    public PedidoBuilder conEstado(EstadoPedido estado) {
        pedido.setEstado(estado);
        return this;
    }

    public PedidoBuilder agregarItem(
            Long platoId,
            String nombrePlato,
            BigDecimal precioUnitario,
            Integer cantidad,
            String notas) {

        ItemPedido item = new ItemPedido();

        item.setPedido(pedido);
        item.setPlatoId(platoId);
        item.setNombrePlato(nombrePlato);
        item.setPrecioUnitario(precioUnitario);
        item.setCantidad(cantidad);
        item.setNotas(notas);
        item.setSubtotal(BigDecimal.ZERO);
        item.setExtras(new ArrayList<>());

        pedido.getItems().add(item);

        return this;
    }

    public PedidoBuilder agregarExtra(
            int indiceItem,
            Long extraId,
            String nombreExtra,
            BigDecimal precioAdicional) {

        if (indiceItem < 0 || indiceItem >= pedido.getItems().size()) {
            throw new IllegalArgumentException("El elemento del pedido no existe");
        }

        ItemPedido item = pedido.getItems().get(indiceItem);

        ItemPedidoExtra extra = new ItemPedidoExtra();

        extra.setItemPedido(item);
        extra.setExtraId(extraId);
        extra.setNombreExtra(nombreExtra);
        extra.setPrecioAdicional(precioAdicional);

        item.getExtras().add(extra);

        return this;
    }

    public PedidoBuilder conValores(
            BigDecimal subtotal,
            BigDecimal impuestos,
            BigDecimal total) {

        pedido.setSubtotal(subtotal);
        pedido.setImpuestos(impuestos);
        pedido.setTotal(total);

        return this;
    }

    public Pedido build() {
        return pedido;
    }
}