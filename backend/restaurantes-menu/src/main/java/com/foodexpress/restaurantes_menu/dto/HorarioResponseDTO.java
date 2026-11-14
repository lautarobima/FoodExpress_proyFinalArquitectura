package com.foodexpress.restaurantes_menu.dto;

import java.time.LocalTime;

import com.foodexpress.restaurantes_menu.model.enums.DiaSemana;

public class HorarioResponseDTO {

    private Long id;
    private Long restauranteId;
    private DiaSemana diaSemana;
    private LocalTime apertura;
    private LocalTime cierre;

    public HorarioResponseDTO(
            Long id,
            Long restauranteId,
            DiaSemana diaSemana,
            LocalTime apertura,
            LocalTime cierre
    ) {
        this.id = id;
        this.restauranteId = restauranteId;
        this.diaSemana = diaSemana;
        this.apertura = apertura;
        this.cierre = cierre;
    }

    public Long getId() {
        return id;
    }

    public Long getRestauranteId() {
        return restauranteId;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public LocalTime getApertura() {
        return apertura;
    }

    public LocalTime getCierre() {
        return cierre;
    }
}