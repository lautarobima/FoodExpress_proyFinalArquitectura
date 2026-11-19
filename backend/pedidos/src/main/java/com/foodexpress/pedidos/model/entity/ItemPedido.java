package com.foodexpress.pedidos.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "item_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "plato_id", nullable = false)
    private Long platoId;

    @Column(name = "nombre_plato", nullable = false, length = 120)
    private String nombrePlato;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(length = 500)
    private String notas;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @OneToMany(
        mappedBy = "itemPedido",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<ItemPedidoExtra> extras = new ArrayList<>();

    public ItemPedido() {
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Long getPlatoId() {
        return platoId;
    }

    public void setPlatoId(Long platoId) {
        this.platoId = platoId;
    }

    public String getNombrePlato() {
        return nombrePlato;
    }

    public void setNombrePlato(String nombrePlato) {
        this.nombrePlato = nombrePlato;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public List<ItemPedidoExtra> getExtras() {
        return extras;
    }

    public void setExtras(List<ItemPedidoExtra> extras) {
        this.extras = extras;
    }
}