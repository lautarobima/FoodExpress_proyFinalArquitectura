package com.foodexpress.notificaciones.model.entity;

import java.time.LocalDateTime;

import com.foodexpress.notificaciones.model.enums.EstadoEnvio;
import com.foodexpress.notificaciones.model.enums.TipoCanal;
import com.foodexpress.notificaciones.model.enums.TipoDestinatario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Historial de cada notificación enviada (o fallida) por canal
@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Column(name = "tipo_evento", nullable = false, length = 40)
    private String tipoEvento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_destinatario", nullable = false, length = 15)
    private TipoDestinatario tipoDestinatario;

    @Column(name = "destinatario_id", nullable = false)
    private Long destinatarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipoCanal canal;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoEnvio estado;

    @Column(name = "detalle_error", length = 300)
    private String detalleError;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    public Notificacion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public TipoDestinatario getTipoDestinatario() {
        return tipoDestinatario;
    }

    public void setTipoDestinatario(TipoDestinatario tipoDestinatario) {
        this.tipoDestinatario = tipoDestinatario;
    }

    public Long getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(Long destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public TipoCanal getCanal() {
        return canal;
    }

    public void setCanal(TipoCanal canal) {
        this.canal = canal;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public EstadoEnvio getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnvio estado) {
        this.estado = estado;
    }

    public String getDetalleError() {
        return detalleError;
    }

    public void setDetalleError(String detalleError) {
        this.detalleError = detalleError;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
}
