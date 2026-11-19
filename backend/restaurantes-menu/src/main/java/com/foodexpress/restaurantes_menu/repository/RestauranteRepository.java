package com.foodexpress.restaurantes_menu.repository;

import com.foodexpress.restaurantes_menu.model.entity.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

    List<Restaurante> findByActivoTrue();

}