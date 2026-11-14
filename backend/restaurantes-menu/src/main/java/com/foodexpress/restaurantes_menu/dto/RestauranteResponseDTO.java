package com.foodexpress.restaurantes_menu.dto;

import com.foodexpress.restaurantes_menu.model.enums.EstadoOperativo;
import java.time.LocalDateTime;

public class RestauranteResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String direccion;
    private String telefono;
    private Boolean activo;
    private EstadoOperativo estadoOperativo;
    private LocalDateTime creadoEn;

    public RestauranteResponseDTO(
            Long id,
            String nombre,
            String descripcion,
            String direccion,
            String telefono,
            Boolean activo,
            EstadoOperativo estadoOperativo,
            LocalDateTime creadoEn) {

        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.telefono = telefono;
        this.activo = activo;
        this.estadoOperativo = estadoOperativo;
        this.creadoEn = creadoEn;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public Boolean getActivo() {
        return activo;
    }

    public EstadoOperativo getEstadoOperativo() {
        return estadoOperativo;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
}