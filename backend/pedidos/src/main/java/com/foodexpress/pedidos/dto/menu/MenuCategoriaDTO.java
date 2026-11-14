
package com.foodexpress.pedidos.dto.menu;

import java.util.List;

public class MenuCategoriaDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private List<MenuPlatoDTO> platos;

    public MenuCategoriaDTO() {}

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

    public List<MenuPlatoDTO> getPlatos() {
        return platos;
    }

    public void setPlatos(List<MenuPlatoDTO> platos) {
        this.platos = platos;
    }
}
