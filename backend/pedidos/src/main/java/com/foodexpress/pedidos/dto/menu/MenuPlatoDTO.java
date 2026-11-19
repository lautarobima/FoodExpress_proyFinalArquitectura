
package com.foodexpress.pedidos.dto.menu;

import java.math.BigDecimal;
import java.util.List;

public class MenuPlatoDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private List<MenuExtraDTO> extras;

    public MenuPlatoDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public List<MenuExtraDTO> getExtras() {
        return extras;
    }

    public void setExtras(List<MenuExtraDTO> extras) {
        this.extras = extras;
    }
}
