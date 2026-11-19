package com.foodexpress.pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class PedidoRequestDTO {

    @NotNull(message = "El cliente es obligatorio")
    @Positive(message = "El ID del cliente debe ser positivo")
    private Long clienteId;

    @NotNull(message = "El restaurante es obligatorio")
    @Positive(message = "El ID del restaurante debe ser positivo")
    private Long restauranteId;

    @NotEmpty(message = "El pedido debe contener al menos un plato")
    @Valid
    private List<ItemPedidoRequestDTO> items;

    public PedidoRequestDTO() {
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(Long restauranteId) {
        this.restauranteId = restauranteId;
    }

    public List<ItemPedidoRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemPedidoRequestDTO> items) {
        this.items = items;
    }
}