
package com.foodexpress.restaurantes_menu.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.foodexpress.restaurantes_menu.dto.HorarioRequestDTO;
import com.foodexpress.restaurantes_menu.dto.HorarioResponseDTO;
import com.foodexpress.restaurantes_menu.service.HorarioRestauranteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurantes/{restauranteId}/horarios")
public class HorarioRestauranteController {

    private final HorarioRestauranteService horarioService;

    public HorarioRestauranteController(
            HorarioRestauranteService horarioService) {
        this.horarioService = horarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HorarioResponseDTO crear(
            @PathVariable Long restauranteId,
            @Valid @RequestBody HorarioRequestDTO dto) {

        return horarioService.crear(restauranteId, dto);
    }

    @GetMapping
    public List<HorarioResponseDTO> listar(
            @PathVariable Long restauranteId) {

        return horarioService.listarPorRestaurante(restauranteId);
    }

    @PutMapping("/{horarioId}")
public HorarioResponseDTO actualizar(
        @PathVariable Long restauranteId,
        @PathVariable Long horarioId,
        @Valid @RequestBody HorarioRequestDTO dto) {

    return horarioService.actualizar(
            restauranteId, horarioId, dto);
}

    @DeleteMapping("/{horarioId}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void eliminar(
        @PathVariable Long restauranteId,
        @PathVariable Long horarioId) {

    horarioService.eliminar(restauranteId, horarioId);
}
}
