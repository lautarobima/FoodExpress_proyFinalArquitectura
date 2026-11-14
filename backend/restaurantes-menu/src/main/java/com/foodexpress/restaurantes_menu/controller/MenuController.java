
package com.foodexpress.restaurantes_menu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodexpress.restaurantes_menu.dto.MenuResponseDTO;
import com.foodexpress.restaurantes_menu.service.MenuService;

@RestController
@RequestMapping("/api/restaurantes/{restauranteId}/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // Consultar el menú completo de un restaurante
    @GetMapping
    public MenuResponseDTO consultarMenuCompleto(
            @PathVariable Long restauranteId) {

        return menuService.consultarMenuCompleto(restauranteId);
    }
}
