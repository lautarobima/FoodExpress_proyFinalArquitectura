
package com.foodexpress.restaurantes_menu.dto;

import java.math.BigDecimal;

import com.foodexpress.restaurantes_menu.model.enums.TipoExtra;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ExtraRequestDTO {

    @NotBlank(message = "El nombre del extra es obligatorio")
    @Size(max = 100)
    private String nombre;

    @Size(max = 250)
    private String descripcion;

    @NotNull(message = "El tipo de extra es obligatorio")
    private TipoExtra tipo;

    @NotNull(message = "El precio adicional es obligatorio")
    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    private BigDecimal precioAdicional;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoExtra getTipo() {
        return tipo;
    }

    public void setTipo(TipoExtra tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPrecioAdicional() {
        return precioAdicional;
    }

    public void setPrecioAdicional(BigDecimal precioAdicional) {
        this.precioAdicional = precioAdicional;
    }
}
