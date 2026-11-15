package com.foodexpress.notificaciones.service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.foodexpress.notificaciones.adapter.CanalNotificacion;
import com.foodexpress.notificaciones.dto.MensajeNotificacion;
import com.foodexpress.notificaciones.messaging.evento.PedidoCanceladoEvento;
import com.foodexpress.notificaciones.messaging.evento.PedidoCreadoEvento;
import com.foodexpress.notificaciones.messaging.evento.PedidoEstadoCambiadoEvento;
import com.foodexpress.notificaciones.model.entity.Notificacion;
import com.foodexpress.notificaciones.model.enums.EstadoEnvio;
import com.foodexpress.notificaciones.model.enums.TipoCanal;
import com.foodexpress.notificaciones.model.enums.TipoDestinatario;
import com.foodexpress.notificaciones.repository.NotificacionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Decide qué se notifica, a quién y por qué canal. El envío concreto se
 * delega a los adaptadores (CanalNotificacion), que Spring inyecta como lista.
 */
@Service
public class NotificacionService {

    private static final Logger log =
            LoggerFactory.getLogger(NotificacionService.class);

    private final NotificacionRepository notificacionRepository;
    private final Map<TipoCanal, CanalNotificacion> canales;

    public NotificacionService(
            NotificacionRepository notificacionRepository,
            List<CanalNotificacion> canalesDisponibles) {

        this.notificacionRepository = notificacionRepository;
        this.canales = new EnumMap<>(TipoCanal.class);
        canalesDisponibles.forEach(canal -> canales.put(canal.tipo(), canal));
    }

    // pedido.creado: confirmación al cliente y alerta al restaurante
    @Transactional
    public void notificarPedidoCreado(PedidoCreadoEvento evento) {

        String detalle = evento.items() == null ? "" : evento.items()
                .stream()
                .map(item -> item.cantidad() + "x " + item.nombrePlato())
                .collect(Collectors.joining(", "));

        enviar(new MensajeNotificacion(
                        evento.pedidoId(), "pedido.creado",
                        TipoDestinatario.CLIENTE, evento.clienteId(),
                        "Pedido #" + evento.pedidoId() + " confirmado",
                        "Recibimos tu pedido por $" + evento.total()
                                + ". Te avisaremos cada cambio de estado."),
                List.of(TipoCanal.EMAIL, TipoCanal.WEBSOCKET));

        enviar(new MensajeNotificacion(
                        evento.pedidoId(), "pedido.creado",
                        TipoDestinatario.RESTAURANTE, evento.restauranteId(),
                        "Nuevo pedido #" + evento.pedidoId(),
                        detalle),
                List.of(TipoCanal.WEBSOCKET));
    }

    // pedido.estado.cambiado: actualización en tiempo real al cliente
    @Transactional
    public void notificarCambioEstado(PedidoEstadoCambiadoEvento evento) {

        boolean listo = "LISTO".equals(evento.estadoNuevo());

        String titulo = listo
                ? "Tu pedido #" + evento.pedidoId() + " está listo para recoger"
                : "Pedido #" + evento.pedidoId() + ": " + evento.estadoNuevo();

        String cuerpo = "Tu pedido pasó de " + evento.estadoAnterior()
                + " a " + evento.estadoNuevo() + ".";

        // Cuando está listo también se envía push para que el cliente lo vea
        List<TipoCanal> canalesCliente = listo
                ? List.of(TipoCanal.WEBSOCKET, TipoCanal.PUSH)
                : List.of(TipoCanal.WEBSOCKET);

        enviar(new MensajeNotificacion(
                        evento.pedidoId(), "pedido.estado.cambiado",
                        TipoDestinatario.CLIENTE, evento.clienteId(),
                        titulo, cuerpo),
                canalesCliente);
    }

    // pedido.cancelado: se alerta a la contraparte de quien canceló
    @Transactional
    public void notificarCancelacion(PedidoCanceladoEvento evento) {

        String motivo = evento.motivo() == null || evento.motivo().isBlank()
                ? "Sin motivo indicado"
                : evento.motivo();

        if ("CLIENTE".equals(evento.canceladoPor())) {

            enviar(new MensajeNotificacion(
                            evento.pedidoId(), "pedido.cancelado",
                            TipoDestinatario.RESTAURANTE, evento.restauranteId(),
                            "El cliente canceló el pedido #" + evento.pedidoId(),
                            "Motivo: " + motivo),
                    List.of(TipoCanal.WEBSOCKET));

        } else {

            enviar(new MensajeNotificacion(
                            evento.pedidoId(), "pedido.cancelado",
                            TipoDestinatario.CLIENTE, evento.clienteId(),
                            "El restaurante canceló tu pedido #" + evento.pedidoId(),
                            "Motivo: " + motivo),
                    List.of(TipoCanal.WEBSOCKET, TipoCanal.EMAIL));
        }
    }

    @Transactional(readOnly = true)
    public List<Notificacion> listarPorPedido(Long pedidoId) {
        return notificacionRepository.findByPedidoIdOrderByFechaEnvioAsc(pedidoId);
    }

    @Transactional(readOnly = true)
    public List<Notificacion> listarPorDestinatario(
            TipoDestinatario tipo,
            Long destinatarioId) {

        return notificacionRepository
                .findByTipoDestinatarioAndDestinatarioIdOrderByFechaEnvioDesc(
                        tipo, destinatarioId);
    }

    // Envía por cada canal y guarda el resultado. Si un canal falla, se
    // registra como FALLIDA y se continúa con los demás.
    private void enviar(MensajeNotificacion mensaje, List<TipoCanal> tiposCanal) {

        for (TipoCanal tipo : tiposCanal) {

            Notificacion registro = crearRegistro(mensaje, tipo);
            CanalNotificacion canal = canales.get(tipo);

            try {
                if (canal == null) {
                    throw new IllegalStateException("Canal no configurado: " + tipo);
                }

                canal.enviar(mensaje);
                registro.setEstado(EstadoEnvio.ENVIADA);

            } catch (RuntimeException ex) {
                log.warn("Falló el envío por {} del pedido {}: {}",
                        tipo, mensaje.pedidoId(), ex.getMessage());
                registro.setEstado(EstadoEnvio.FALLIDA);
                registro.setDetalleError(recortar(ex.getMessage(), 300));
            }

            notificacionRepository.save(registro);
        }
    }

    private Notificacion crearRegistro(MensajeNotificacion mensaje, TipoCanal tipo) {

        Notificacion n = new Notificacion();
        n.setPedidoId(mensaje.pedidoId());
        n.setTipoEvento(mensaje.tipoEvento());
        n.setTipoDestinatario(mensaje.tipoDestinatario());
        n.setDestinatarioId(mensaje.destinatarioId());
        n.setCanal(tipo);
        n.setTitulo(recortar(mensaje.titulo(), 150));
        n.setMensaje(recortar(mensaje.cuerpo(), 500));
        n.setFechaEnvio(LocalDateTime.now());
        return n;
    }

    private String recortar(String texto, int max) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= max ? texto : texto.substring(0, max);
    }
}
