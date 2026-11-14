
package com.foodexpress.restaurantes_menu.model.entity;

import com.foodexpress.restaurantes_menu.model.enums.EstadoOperativo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "restaurante")
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nombre;

    @Size(max = 500)
    @Column(length = 500)
    private String descripcion;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String direccion;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_operativo", nullable = false, length = 15)
    private EstadoOperativo estadoOperativo = EstadoOperativo.CERRADO;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    // Constructor vacío requerido por JPA
    public Restaurante() {
    }

    // Asignar fecha de creación antes de guardar
    @PrePersist
    protected void antesDeGuardar() {
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now();
        }
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public EstadoOperativo getEstadoOperativo() {
        return estadoOperativo;
    }

    public void setEstadoOperativo(EstadoOperativo estadoOperativo) {
        this.estadoOperativo = estadoOperativo;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
