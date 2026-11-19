
package com.foodexpress.restaurantes_menu.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "plato_extra")
public class PlatoExtra {

    @EmbeddedId
    private PlatoExtraId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("platoId")
    @JoinColumn(name = "plato_id", nullable = false)
    private Plato plato;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("extraId")
    @JoinColumn(name = "extra_id", nullable = false)
    private Extra extra;

    public PlatoExtra() {
    }

    public PlatoExtra(Plato plato, Extra extra) {
        this.plato = plato;
        this.extra = extra;
        this.id = new PlatoExtraId(
            plato.getId(),
            extra.getId()
        );
    }

    public PlatoExtraId getId() {
        return id;
    }

    public void setId(PlatoExtraId id) {
        this.id = id;
    }

    public Plato getPlato() {
        return plato;
    }

    public void setPlato(Plato plato) {
        this.plato = plato;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }
}
