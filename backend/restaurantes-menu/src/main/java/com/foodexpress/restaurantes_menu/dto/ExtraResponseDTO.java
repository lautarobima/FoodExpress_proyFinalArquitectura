
package com.foodexpress.restaurantes_menu.dto;

import java.math.BigDecimal;

import com.foodexpress.restaurantes_menu.model.enums.TipoExtra;

public class ExtraResponseDTO {

    private Long id;
    private Long restauranteId;
    private String nombre;
    private String descripcion;
    private TipoExtra tipo;
    private BigDecimal precioAdicional;
    private Boolean disponible;

    public ExtraResponseDTO(
            Long id,
            Long restauranteId,
            String nombre,
            String descripcion,
            TipoExtra tipo,
            BigDecimal precioAdicional,
            Boolean disponible) {

        this.id = id;
        this.restauranteId = restauranteId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.precioAdicional = precioAdicional;
        this.disponible = disponible;
    }

    public Long getId() {
        return id;
    }

    public Long getRestauranteId() {
        return restauranteId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public TipoExtra getTipo() {
        return tipo;
    }

    public BigDecimal getPrecioAdicional() {
        return precioAdicional;
    }

    public Boolean getDisponible() {
        return disponible;
    }
}
