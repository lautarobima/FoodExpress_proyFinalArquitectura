
package com.foodexpress.restaurantes_menu.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PlatoResponseDTO {

    private Long id;
    private Long categoriaId;
    private String categoriaNombre;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Boolean disponible;
    private LocalDateTime creadoEn;
    private Integer stock;

    public PlatoResponseDTO(
            Long id,
            Long categoriaId,
            String categoriaNombre,
            String nombre,
            String descripcion,
            BigDecimal precio,
            Boolean disponible,
            LocalDateTime creadoEn,
            Integer stock) {

        this.id = id;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = disponible;
        this.creadoEn = creadoEn;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public Integer getStock() {
        return stock;
    }
}
