package com.foodexpress.restaurantes_menu.dto;

public class PlatoExtraResponseDTO {

    private Long platoId;
    private String platoNombre;
    private Long extraId;
    private String extraNombre;

    public PlatoExtraResponseDTO(
            Long platoId,
            String platoNombre,
            Long extraId,
            String extraNombre
    ) {
        this.platoId = platoId;
        this.platoNombre = platoNombre;
        this.extraId = extraId;
        this.extraNombre = extraNombre;
    }

    public Long getPlatoId() {
        return platoId;
    }

    public String getPlatoNombre() {
        return platoNombre;
    }

    public Long getExtraId() {
        return extraId;
    }

    public String getExtraNombre() {
        return extraNombre;
    }
}