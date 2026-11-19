
package com.foodexpress.restaurantes_menu.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.foodexpress.restaurantes_menu.dto.CategoriaRequestDTO;
import com.foodexpress.restaurantes_menu.dto.CategoriaResponseDTO;
import com.foodexpress.restaurantes_menu.service.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurantes/{restauranteId}/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // Crear una categoría para un restaurante
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponseDTO crear(
            @PathVariable Long restauranteId,
            @Valid @RequestBody CategoriaRequestDTO dto
    ) {
        return categoriaService.crear(restauranteId, dto);
    }

    // Listar las categorías de un restaurante
    @GetMapping
    public List<CategoriaResponseDTO> listar(
            @PathVariable Long restauranteId
    ) {
        return categoriaService.listarPorRestaurante(restauranteId);
    }

    // Actualizar una categoría
    @PutMapping("/{categoriaId}")
public CategoriaResponseDTO actualizar(
        @PathVariable Long restauranteId,
        @PathVariable Long categoriaId,
        @Valid @RequestBody CategoriaRequestDTO dto) {

    return categoriaService.actualizar(restauranteId, categoriaId, dto);
}

    @PatchMapping("/{categoriaId}/desactivar")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void desactivar(
        @PathVariable Long restauranteId,
        @PathVariable Long categoriaId) {

    categoriaService.desactivar(restauranteId, categoriaId);
}
}
