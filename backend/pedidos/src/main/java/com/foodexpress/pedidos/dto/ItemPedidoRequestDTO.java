package com.foodexpress.pedidos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ItemPedidoRequestDTO {

    @NotNull(message = "El ID del plato es obligatorio")
    @Positive(message = "El ID del plato debe ser positivo")
    private Long platoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser como mínimo 1")
    private Integer cantidad;

    private List<@NotNull @Positive Long> extrasIds;

    @Size(max = 500, message = "Las notas no pueden superar 500 caracteres")
    private String notas;

    public ItemPedidoRequestDTO() {
    }

    public Long getPlatoId() {
        return platoId;
    }

    public void setPlatoId(Long platoId) {
        this.platoId = platoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public List<Long> getExtrasIds() {
        return extrasIds;
    }

    public void setExtrasIds(List<Long> extrasIds) {
        this.extrasIds = extrasIds;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}