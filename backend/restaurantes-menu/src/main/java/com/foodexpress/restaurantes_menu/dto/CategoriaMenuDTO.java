
package com.foodexpress.restaurantes_menu.dto;

import java.util.List;

public class CategoriaMenuDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private List<PlatoMenuDTO> platos;

    public CategoriaMenuDTO(
            Long id,
            String nombre,
            String descripcion,
            List<PlatoMenuDTO> platos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.platos = platos;
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

    public List<PlatoMenuDTO> getPlatos() {
        return platos;
    }
}
