package com.foodexpress.restaurantes_menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodexpress.restaurantes_menu.model.entity.PlatoExtra;
import com.foodexpress.restaurantes_menu.model.entity.PlatoExtraId;

public interface PlatoExtraRepository extends JpaRepository<PlatoExtra, PlatoExtraId> {

    List<PlatoExtra> findByPlato_Id(Long platoId);

    List<PlatoExtra> findByExtra_Id(Long extraId);

    boolean existsByPlato_IdAndExtra_Id(Long platoId, Long extraId);
}