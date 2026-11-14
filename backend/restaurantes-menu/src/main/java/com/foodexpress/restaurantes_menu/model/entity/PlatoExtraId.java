package com.foodexpress.restaurantes_menu.model.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class PlatoExtraId implements Serializable {

    private Long platoId;
    private Long extraId;

    public PlatoExtraId() {
    }

    public PlatoExtraId(Long platoId, Long extraId) {
        this.platoId = platoId;
        this.extraId = extraId;
    }

    public Long getPlatoId() {
        return platoId;
    }

    public void setPlatoId(Long platoId) {
        this.platoId = platoId;
    }

    public Long getExtraId() {
        return extraId;
    }

    public void setExtraId(Long extraId) {
        this.extraId = extraId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlatoExtraId that)) return false;
        return Objects.equals(platoId, that.platoId)
                && Objects.equals(extraId, that.extraId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(platoId, extraId);
    }
}