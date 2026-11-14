package com.foodexpress.restaurantes_menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodexpress.restaurantes_menu.model.entity.Extra;

public interface ExtraRepository extends JpaRepository<Extra, Long> {

    List<Extra> findByRestaurante_Id(Long restauranteId);

    List<Extra> findByDisponibleTrue();

    boolean existsByRestaurante_IdAndNombreIgnoreCase(
            Long restauranteId, String nombre);

    boolean existsByRestaurante_IdAndNombreIgnoreCaseAndIdNot(
            Long restauranteId, String nombre, Long id);
}