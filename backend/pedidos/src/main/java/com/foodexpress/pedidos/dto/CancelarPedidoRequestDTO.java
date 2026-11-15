package com.foodexpress.pedidos.dto;

import com.foodexpress.pedidos.model.enums.ActorCancelacion;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CancelarPedidoRequestDTO {

    @NotNull(message = "Se debe indicar quién cancela: CLIENTE o RESTAURANTE")
    private ActorCancelacion canceladoPor;

    @Size(max = 300, message = "El motivo no puede superar 300 caracteres")
    private String motivo;

    public CancelarPedidoRequestDTO() {
    }

    public ActorCancelacion getCanceladoPor() {
        return canceladoPor;
    }

    public void setCanceladoPor(ActorCancelacion canceladoPor) {
        this.canceladoPor = canceladoPor;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
