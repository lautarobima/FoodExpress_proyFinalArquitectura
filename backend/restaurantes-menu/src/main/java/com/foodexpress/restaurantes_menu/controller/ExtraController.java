
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.foodexpress.restaurantes_menu.dto.ExtraRequestDTO;
import com.foodexpress.restaurantes_menu.dto.ExtraResponseDTO;
import com.foodexpress.restaurantes_menu.service.ExtraService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurantes/{restauranteId}/extras")
public class ExtraController {

    private final ExtraService extraService;

    public ExtraController(ExtraService extraService) {
        this.extraService = extraService;
    }

    // Registrar un extra
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExtraResponseDTO crear(
            @PathVariable Long restauranteId,
            @Valid @RequestBody ExtraRequestDTO dto
    ) {
        return extraService.crear(restauranteId, dto);
    }

    // Listar extras de un restaurante
    @GetMapping
    public List<ExtraResponseDTO> listar(
            @PathVariable Long restauranteId
    ) {
        return extraService.listarPorRestaurante(restauranteId);
    }

    @GetMapping("/{id}")
public ExtraResponseDTO buscarPorId(
        @PathVariable Long restauranteId,
        @PathVariable Long id) {

    return extraService.buscarPorId(restauranteId, id);
}

    @PutMapping("/{id}")
public ExtraResponseDTO actualizar(
        @PathVariable Long restauranteId,
        @PathVariable Long id,
        @Valid @RequestBody ExtraRequestDTO dto) {

    return extraService.actualizar(restauranteId, id, dto);
}

    @PatchMapping("/{id}/disponibilidad")
public ExtraResponseDTO cambiarDisponibilidad(
        @PathVariable Long restauranteId,
        @PathVariable Long id,
        @RequestParam Boolean disponible) {

    return extraService.cambiarDisponibilidad(
            restauranteId, id, disponible);
}
}
