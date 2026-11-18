package com.foodexpress.restaurantes_menu.service;

import com.foodexpress.restaurantes_menu.dto.PlatoRequestDTO;
import com.foodexpress.restaurantes_menu.dto.PlatoResponseDTO;

// Separada de MenuQueryService, de solo consulta
public interface MenuAdminService {
    PlatoResponseDTO crear(PlatoRequestDTO dto);
    PlatoResponseDTO actualizar(Long id, PlatoRequestDTO dto);
    PlatoResponseDTO cambiarDisponibilidad(Long id, Boolean disponible);
    void ajustarStock(Long platoId, int delta);
}