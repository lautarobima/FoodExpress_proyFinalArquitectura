package com.foodexpress.restaurantes_menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodexpress.restaurantes_menu.model.entity.HorarioRestaurante;
import com.foodexpress.restaurantes_menu.model.enums.DiaSemana;

public interface HorarioRestauranteRepository extends JpaRepository<HorarioRestaurante, Long> {

    List<HorarioRestaurante> findByRestaurante_Id(Long restauranteId);
    List<HorarioRestaurante> findByRestaurante_IdAndDiaSemana(
        Long restauranteId,
        DiaSemana diaSemana
);
}