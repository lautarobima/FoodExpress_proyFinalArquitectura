
package com.foodexpress.pedidos.dto;

import java.math.BigDecimal;

public class ItemPedidoExtraResponseDTO {

    private Long extraId;
    private String nombreExtra;
    private BigDecimal precioAdicional;

    public ItemPedidoExtraResponseDTO() {}

    public Long getExtraId() { return extraId; }
    public void setExtraId(Long extraId) { this.extraId = extraId; }

    public String getNombreExtra() { return nombreExtra; }
    public void setNombreExtra(String nombreExtra) { this.nombreExtra = nombreExtra; }

    public BigDecimal getPrecioAdicional() { return precioAdicional; }
    public void setPrecioAdicional(BigDecimal precioAdicional) {
        this.precioAdicional = precioAdicional;
    }
}
