
package com.foodexpress.pedidos.dto;

import java.math.BigDecimal;
import java.util.List;

public class ItemPedidoResponseDTO {

    private Long id;
    private Long platoId;
    private String nombrePlato;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private String notas;
    private BigDecimal subtotal;
    private List<ItemPedidoExtraResponseDTO> extras;

    public ItemPedidoResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPlatoId() { return platoId; }
    public void setPlatoId(Long platoId) { this.platoId = platoId; }

    public String getNombrePlato() { return nombrePlato; }
    public void setNombrePlato(String nombrePlato) { this.nombrePlato = nombrePlato; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public List<ItemPedidoExtraResponseDTO> getExtras() { return extras; }
    public void setExtras(List<ItemPedidoExtraResponseDTO> extras) { this.extras = extras; }
}
