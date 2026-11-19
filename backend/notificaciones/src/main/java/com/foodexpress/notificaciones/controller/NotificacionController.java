package com.foodexpress.notificaciones.controller;

import java.util.List;

import com.foodexpress.notificaciones.dto.NotificacionResponseDTO;
import com.foodexpress.notificaciones.model.entity.Notificacion;
import com.foodexpress.notificaciones.model.enums.TipoDestinatario;
import com.foodexpress.notificaciones.service.NotificacionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Consulta del historial de notificaciones
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    // ?pedidoId=1  |  ?clienteId=1  |  ?restauranteId=1
    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listar(
            @RequestParam(required = false) Long pedidoId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long restauranteId) {

        List<Notificacion> resultado;

        if (pedidoId != null) {
            resultado = notificacionService.listarPorPedido(pedidoId);
        } else if (clienteId != null) {
            resultado = notificacionService.listarPorDestinatario(
                    TipoDestinatario.CLIENTE, clienteId);
        } else if (restauranteId != null) {
            resultado = notificacionService.listarPorDestinatario(
                    TipoDestinatario.RESTAURANTE, restauranteId);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                resultado.stream().map(NotificacionResponseDTO::desde).toList());
    }
}
