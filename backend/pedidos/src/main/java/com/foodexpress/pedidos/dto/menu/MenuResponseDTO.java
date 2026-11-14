
package com.foodexpress.pedidos.dto.menu;

import java.util.List;

public class MenuResponseDTO {

    private MenuRestauranteDTO restaurante;
    private List<MenuCategoriaDTO> categorias;

    public MenuResponseDTO() {}

    public MenuRestauranteDTO getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(MenuRestauranteDTO restaurante) {
        this.restaurante = restaurante;
    }

    public List<MenuCategoriaDTO> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<MenuCategoriaDTO> categorias) {
        this.categorias = categorias;
    }
}
