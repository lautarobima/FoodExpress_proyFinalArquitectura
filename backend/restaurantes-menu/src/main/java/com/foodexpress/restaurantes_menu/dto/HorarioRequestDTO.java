package com.foodexpress.restaurantes_menu.dto;

import java.time.LocalTime;

import com.foodexpress.restaurantes_menu.model.enums.DiaSemana;

import jakarta.validation.constraints.NotNull;

public class HorarioRequestDTO {

    @NotNull(message = "El día de la semana es obligatorio")
    private DiaSemana diaSemana;

    @NotNull(message = "La hora de apertura es obligatoria")
    private LocalTime apertura;

    @NotNull(message = "La hora de cierre es obligatoria")
    private LocalTime cierre;

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getApertura() {
        return apertura;
    }

    public void setApertura(LocalTime apertura) {
        this.apertura = apertura;
    }

    public LocalTime getCierre() {
        return cierre;
    }

    public void setCierre(LocalTime cierre) {
        this.cierre = cierre;
    }
}