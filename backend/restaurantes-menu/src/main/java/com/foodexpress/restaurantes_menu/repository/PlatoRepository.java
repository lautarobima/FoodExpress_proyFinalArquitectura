package com.foodexpress.restaurantes_menu.repository;

import com.foodexpress.restaurantes_menu.model.entity.Plato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlatoRepository extends JpaRepository<Plato, Long> {

    List<Plato> findByCategoria_Id(Long categoriaId);

    List<Plato> findByDisponibleTrue();
}