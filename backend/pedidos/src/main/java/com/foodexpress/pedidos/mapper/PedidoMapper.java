
package com.foodexpress.pedidos.mapper;

import com.foodexpress.pedidos.dto.PedidoResponseDTO;
import com.foodexpress.pedidos.dto.ItemPedidoResponseDTO;
import com.foodexpress.pedidos.dto.ItemPedidoExtraResponseDTO;

import com.foodexpress.pedidos.model.entity.Pedido;
import com.foodexpress.pedidos.model.entity.ItemPedido;
import com.foodexpress.pedidos.model.entity.ItemPedidoExtra;

import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PedidoMapper {

    public PedidoResponseDTO convertirADTO(Pedido pedido) {

        PedidoResponseDTO respuesta = new PedidoResponseDTO();

        respuesta.setId(pedido.getId());
        respuesta.setClienteId(pedido.getClienteId());
        respuesta.setRestauranteId(pedido.getRestauranteId());
        respuesta.setFechaCreacion(pedido.getFechaCreacion());
        respuesta.setEstado(pedido.getEstado());
        respuesta.setSubtotal(pedido.getSubtotal());
        respuesta.setImpuestos(pedido.getImpuestos());
        respuesta.setTotal(pedido.getTotal());
        respuesta.setMotivoCancelacion(pedido.getMotivoCancelacion());
        respuesta.setFechaActualizacion(pedido.getFechaActualizacion());

        respuesta.setItems(
                pedido.getItems()
                        .stream()
                        .map(this::convertirItemADTO)
                        .collect(Collectors.toList())
        );

        return respuesta;
    }

    private ItemPedidoResponseDTO convertirItemADTO(
            ItemPedido item) {

        ItemPedidoResponseDTO respuesta =
                new ItemPedidoResponseDTO();

        respuesta.setId(item.getId());
        respuesta.setPlatoId(item.getPlatoId());
        respuesta.setNombrePlato(item.getNombrePlato());
        respuesta.setPrecioUnitario(item.getPrecioUnitario());
        respuesta.setCantidad(item.getCantidad());
        respuesta.setNotas(item.getNotas());
        respuesta.setSubtotal(item.getSubtotal());

        respuesta.setExtras(
                item.getExtras()
                        .stream()
                        .map(this::convertirExtraADTO)
                        .collect(Collectors.toList())
        );

        return respuesta;
    }

    private ItemPedidoExtraResponseDTO convertirExtraADTO(
            ItemPedidoExtra extra) {

        ItemPedidoExtraResponseDTO respuesta =
                new ItemPedidoExtraResponseDTO();

        respuesta.setExtraId(extra.getExtraId());
        respuesta.setNombreExtra(extra.getNombreExtra());
        respuesta.setPrecioAdicional(extra.getPrecioAdicional());

        return respuesta;
    }
}
