
package com.foodexpress.restaurantes_menu.controller;

import java.util.List;

import com.foodexpress.restaurantes_menu.service.MenuAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.foodexpress.restaurantes_menu.dto.PlatoRequestDTO;
import com.foodexpress.restaurantes_menu.dto.PlatoResponseDTO;
import com.foodexpress.restaurantes_menu.service.PlatoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/platos")
public class PlatoController {

    private final PlatoService platoService;
    private final MenuAdminService menuAdminService;

    public PlatoController(PlatoService platoService, MenuAdminService menuAdminService) {
        this.platoService = platoService;
        this.menuAdminService = menuAdminService;
    }

    // Registrar un plato
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlatoResponseDTO crear(
            @Valid @RequestBody PlatoRequestDTO dto
    ) {
        return menuAdminService.crear(dto);
    }

    // Consultar los platos de una categoría
    @GetMapping("/categoria/{categoriaId}")
    public List<PlatoResponseDTO> listarPorCategoria(
            @PathVariable Long categoriaId
    ) {
        return platoService.listarPorCategoria(categoriaId);
    }

    // Consultar un plato por ID
    @GetMapping("/{id}")
    public PlatoResponseDTO buscarPorId(
            @PathVariable Long id
    ) {
        return platoService.buscarPorId(id);
    }

    // Actualizar un plato
    @PutMapping("/{id}")
    public PlatoResponseDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PlatoRequestDTO dto
    ) {
        return menuAdminService.actualizar(id, dto);
    }

    // Cambiar disponibilidad de un plato
    @PatchMapping("/{id}/disponibilidad")
    public PlatoResponseDTO cambiarDisponibilidad(
            @PathVariable Long id,
            @RequestParam Boolean disponible
    ) {
        return menuAdminService.cambiarDisponibilidad(id, disponible);
    }
}
