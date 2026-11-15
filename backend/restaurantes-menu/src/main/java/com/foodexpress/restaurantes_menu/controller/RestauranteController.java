
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

import com.foodexpress.restaurantes_menu.dto.RestauranteRequestDTO;
import com.foodexpress.restaurantes_menu.dto.RestauranteResponseDTO;
import com.foodexpress.restaurantes_menu.model.enums.EstadoOperativo;
import com.foodexpress.restaurantes_menu.service.RestauranteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    // Registrar un restaurante
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestauranteResponseDTO crear(
            @Valid @RequestBody RestauranteRequestDTO dto
    ) {
        return restauranteService.crear(dto);
    }

    // Listar restaurantes activos
    @GetMapping
    public List<RestauranteResponseDTO> listarActivos() {
        return restauranteService.listarActivos();
    }

    // Consultar un restaurante por ID
    @GetMapping("/{id}")
    public RestauranteResponseDTO buscarPorId(@PathVariable Long id) {
        return restauranteService.buscarPorId(id);
    }

    // Actualizar un restaurante
    @PutMapping("/{id}")
    public RestauranteResponseDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RestauranteRequestDTO dto
    ) {
        return restauranteService.actualizar(id, dto);
    }

    // Abrir o cerrar el restaurante: ?estado=ABIERTO | CERRADO
    @PatchMapping("/{id}/estado-operativo")
    public RestauranteResponseDTO cambiarEstadoOperativo(
            @PathVariable Long id,
            @RequestParam EstadoOperativo estado
    ) {
        return restauranteService.cambiarEstadoOperativo(id, estado);
    }

    // Desactivar un restaurante
    @PatchMapping("/{id}/desactivar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        restauranteService.desactivar(id);
    }
}
