
package com.foodexpress.restaurantes_menu.model.entity;

import com.foodexpress.restaurantes_menu.model.enums.TipoExtra;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(
    name = "extra",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_extra_restaurante_nombre",
            columnNames = {"restaurante_id", "nombre"}
        )
    }
)
public class Extra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(max = 250)
    @Column(length = 250)
    private String descripcion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoExtra tipo;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(name = "precio_adicional", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioAdicional;

    @Column(nullable = false)
    private Boolean disponible = true;

    public Extra() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoExtra getTipo() {
        return tipo;
    }

    public void setTipo(TipoExtra tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPrecioAdicional() {
        return precioAdicional;
    }

    public void setPrecioAdicional(BigDecimal precioAdicional) {
        this.precioAdicional = precioAdicional;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }
}
