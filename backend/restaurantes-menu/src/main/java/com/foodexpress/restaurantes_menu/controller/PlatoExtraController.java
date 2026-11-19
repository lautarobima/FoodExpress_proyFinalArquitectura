
package com.foodexpress.restaurantes_menu.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.foodexpress.restaurantes_menu.dto.PlatoExtraResponseDTO;
import com.foodexpress.restaurantes_menu.service.PlatoExtraService;

@RestController
@RequestMapping("/api/platos/{platoId}/extras")
public class PlatoExtraController {

    private final PlatoExtraService platoExtraService;

    public PlatoExtraController(PlatoExtraService platoExtraService) {
        this.platoExtraService = platoExtraService;
    }

    // Asociar un extra a un plato
    @PostMapping("/{extraId}")
    @ResponseStatus(HttpStatus.CREATED)
    public PlatoExtraResponseDTO agregarExtra(
            @PathVariable Long platoId,
            @PathVariable Long extraId
    ) {
        return platoExtraService.agregarExtra(platoId, extraId);
    }

    // Consultar los extras asociados a un plato
    @GetMapping
    public List<PlatoExtraResponseDTO> listarExtras(
            @PathVariable Long platoId
    ) {
        return platoExtraService.listarExtrasPorPlato(platoId);
    }

    // Quitar un extra de un plato
    @DeleteMapping("/{extraId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quitarExtra(
            @PathVariable Long platoId,
            @PathVariable Long extraId
    ) {
        platoExtraService.quitarExtra(platoId, extraId);
    }
}
