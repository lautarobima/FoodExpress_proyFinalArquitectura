
package com.foodexpress.restaurantes_menu.dto;

import java.math.BigDecimal;

import com.foodexpress.restaurantes_menu.model.enums.TipoExtra;

public class ExtraMenuDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private TipoExtra tipo;
    private BigDecimal precioAdicional;

    public ExtraMenuDTO(
            Long id,
            String nombre,
            String descripcion,
            TipoExtra tipo,
            BigDecimal precioAdicional) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.precioAdicional = precioAdicional;
    }

    public Long getId() {
        return id;
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
}
