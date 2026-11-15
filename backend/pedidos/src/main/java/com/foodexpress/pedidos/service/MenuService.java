
package com.foodexpress.pedidos.service;

import com.foodexpress.pedidos.dto.menu.MenuCategoriaDTO;
import com.foodexpress.pedidos.dto.menu.MenuExtraDTO;
import com.foodexpress.pedidos.dto.menu.MenuPlatoDTO;
import com.foodexpress.pedidos.dto.menu.MenuResponseDTO;
import com.foodexpress.pedidos.service.port.MenuPort;

import org.springframework.stereotype.Service;

@Service
public class MenuService {

    private final MenuPort menuPort;

    public MenuService(MenuPort menuPort) {
        this.menuPort = menuPort;
    }

    // Consultar el menú completo de un restaurante
    public MenuResponseDTO consultarMenu(Long restauranteId) {
        return menuPort.obtenerMenu(restauranteId);
    }

    // Buscar un plato dentro del menú
    public MenuPlatoDTO buscarPlato(
            MenuResponseDTO menu,
            Long platoId) {

        for (MenuCategoriaDTO categoria : menu.getCategorias()) {

            for (MenuPlatoDTO plato : categoria.getPlatos()) {

                if (plato.getId().equals(platoId)) {
                    return plato;
                }
            }
        }

        throw new IllegalArgumentException(
                "El plato solicitado no existe o no está disponible en el menú");
    }

    // Buscar un extra dentro de un plato
    public MenuExtraDTO buscarExtra(
            MenuPlatoDTO plato,
            Long extraId) {

        for (MenuExtraDTO extra : plato.getExtras()) {

            if (extra.getId().equals(extraId)) {
                return extra;
            }
        }

        throw new IllegalArgumentException(
                "El extra solicitado no está disponible para este plato");
    }
}
