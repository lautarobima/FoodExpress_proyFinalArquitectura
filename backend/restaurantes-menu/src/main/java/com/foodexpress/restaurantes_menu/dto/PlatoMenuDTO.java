
package com.foodexpress.restaurantes_menu.dto;

import java.math.BigDecimal;
import java.util.List;

public class PlatoMenuDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private List<ExtraMenuDTO> extras;

    public PlatoMenuDTO(
            Long id,
            String nombre,
            String descripcion,
            BigDecimal precio,
            List<ExtraMenuDTO> extras) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.extras = extras;
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public List<ExtraMenuDTO> getExtras() {
        return extras;
    }
}
