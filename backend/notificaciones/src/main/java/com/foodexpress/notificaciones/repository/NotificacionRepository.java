package com.foodexpress.notificaciones.repository;

import java.util.List;

import com.foodexpress.notificaciones.model.entity.Notificacion;
import com.foodexpress.notificaciones.model.enums.TipoDestinatario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByPedidoIdOrderByFechaEnvioAsc(Long pedidoId);

    List<Notificacion> findByTipoDestinatarioAndDestinatarioIdOrderByFechaEnvioDesc(
            TipoDestinatario tipoDestinatario,
            Long destinatarioId);
}
