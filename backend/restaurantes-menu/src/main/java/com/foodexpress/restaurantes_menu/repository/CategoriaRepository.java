package com.foodexpress.restaurantes_menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodexpress.restaurantes_menu.model.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByRestaurante_Id(Long restauranteId);

    boolean existsByRestaurante_IdAndNombreIgnoreCase(Long restauranteId, String nombre);

    boolean existsByRestaurante_IdAndNombreIgnoreCaseAndIdNot(
            Long restauranteId, String nombre, Long id);
}