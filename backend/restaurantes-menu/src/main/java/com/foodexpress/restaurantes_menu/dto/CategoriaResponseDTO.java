
package com.foodexpress.restaurantes_menu.dto;

public class CategoriaResponseDTO {

    private Long id;
    private Long restauranteId;
    private String nombre;
    private String descripcion;
    private Boolean activa;

    public CategoriaResponseDTO(
            Long id,
            Long restauranteId,
            String nombre,
            String descripcion,
            Boolean activa) {

        this.id = id;
        this.restauranteId = restauranteId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activa = activa;
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

    public Boolean getActiva() {
        return activa;
    }
}
