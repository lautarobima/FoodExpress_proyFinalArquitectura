
package com.foodexpress.restaurantes_menu.dto;

import java.util.List;

public class MenuResponseDTO {

    private RestauranteResponseDTO restaurante;
    private List<CategoriaMenuDTO> categorias;

    public MenuResponseDTO(
            RestauranteResponseDTO restaurante,
            List<CategoriaMenuDTO> categorias) {
        this.restaurante = restaurante;
        this.categorias = categorias;
    }

    public RestauranteResponseDTO getRestaurante() {
        return restaurante;
    }

    public List<CategoriaMenuDTO> getCategorias() {
        return categorias;
    }
}
