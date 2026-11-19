package com.foodexpress.pedidos.service;

import com.foodexpress.pedidos.dto.CancelarPedidoRequestDTO;
import com.foodexpress.pedidos.dto.ItemPedidoRequestDTO;
import com.foodexpress.pedidos.dto.PedidoRequestDTO;
import com.foodexpress.pedidos.dto.menu.MenuExtraDTO;
import com.foodexpress.pedidos.dto.menu.MenuPlatoDTO;
import com.foodexpress.pedidos.dto.menu.MenuResponseDTO;
import com.foodexpress.pedidos.exception.PedidoNoEncontradoException;
import com.foodexpress.pedidos.exception.ReglaNegocioException;
import com.foodexpress.pedidos.messaging.evento.ItemEvento;
import com.foodexpress.pedidos.messaging.evento.PedidoCanceladoEvento;
import com.foodexpress.pedidos.messaging.evento.PedidoCreadoEvento;
import com.foodexpress.pedidos.messaging.evento.PedidoEstadoCambiadoEvento;
import com.foodexpress.pedidos.model.entity.Pedido;
import com.foodexpress.pedidos.model.enums.EstadoPedido;
import com.foodexpress.pedidos.patterns.builder.PedidoBuilder;
import com.foodexpress.pedidos.repository.PedidoRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PedidoService {

    private static final String RESTAURANTE_ABIERTO = "ABIERTO";

    private final PedidoRepository pedidoRepository;
    private final MenuService menuService;
    private final PedidoCalculoService calculoService;
    private final ApplicationEventPublisher eventPublisher;
    private final BigDecimal tasaImpuesto;

    public PedidoService(
            PedidoRepository pedidoRepository,
            MenuService menuService,
            PedidoCalculoService calculoService,
            ApplicationEventPublisher eventPublisher,
            @Value("${pedidos.impuesto.tasa}") BigDecimal tasaImpuesto) {

        this.pedidoRepository = pedidoRepository;
        this.menuService = menuService;
        this.calculoService = calculoService;
        this.eventPublisher = eventPublisher;
        this.tasaImpuesto = tasaImpuesto;
    }

    @Transactional
    public Pedido crearPedido(PedidoRequestDTO solicitud) {

        // 1. Consultar el menú del restaurante
        MenuResponseDTO menu = menuService.consultarMenu(
                solicitud.getRestauranteId()
        );

        // 2. Validar que el restaurante esté activo y abierto
        validarRestaurante(menu, solicitud.getRestauranteId());

        // 3. Preparar el Builder
        PedidoBuilder builder = new PedidoBuilder();

        builder.conCliente(solicitud.getClienteId())
                .conRestaurante(solicitud.getRestauranteId())
                .conFechaCreacion(LocalDateTime.now())
                .conEstado(EstadoPedido.RECIBIDO);

        List<BigDecimal> subtotalesItems = new ArrayList<>();

        // 4. Recorrer los platos solicitados
        for (ItemPedidoRequestDTO itemSolicitud : solicitud.getItems()) {

            MenuPlatoDTO plato = menuService.buscarPlato(
                    menu,
                    itemSolicitud.getPlatoId()
            );

            List<MenuExtraDTO> extrasSeleccionados =
                    validarExtras(plato, itemSolicitud.getExtrasIds());

            // 5. Calcular el subtotal del plato
            BigDecimal subtotalItem = calculoService.calcularSubtotalItem(
                    plato,
                    extrasSeleccionados,
                    itemSolicitud.getCantidad()
            );

            subtotalesItems.add(subtotalItem);

            // 6. Agregar el plato y sus extras al Builder
            int indiceItem = subtotalesItems.size() - 1;

            builder.agregarItem(
                    plato.getId(),
                    plato.getNombre(),
                    plato.getPrecio(),
                    itemSolicitud.getCantidad(),
                    itemSolicitud.getNotas(),
                    subtotalItem
            );

            for (MenuExtraDTO extra : extrasSeleccionados) {
                builder.agregarExtra(
                        indiceItem,
                        extra.getId(),
                        extra.getNombre(),
                        extra.getPrecioAdicional()
                );
            }
        }

        // 7. Calcular los valores generales del pedido
        BigDecimal subtotal = calculoService.calcularSubtotalPedido(
                subtotalesItems
        );

        BigDecimal impuestos = calculoService.calcularImpuestos(
                subtotal,
                tasaImpuesto
        );

        BigDecimal total = calculoService.calcularTotal(
                subtotal,
                impuestos
        );

        builder.conValores(subtotal, impuestos, total);

        // 8. Guardar en PostgreSQL
        Pedido pedidoGuardado = pedidoRepository.save(builder.build());

        // 9. Evento pedido.creado (se envía a RabbitMQ después del commit)
        eventPublisher.publishEvent(new PedidoCreadoEvento(
                pedidoGuardado.getId(),
                pedidoGuardado.getClienteId(),
                pedidoGuardado.getRestauranteId(),
                pedidoGuardado.getTotal(),
                itemsEvento(pedidoGuardado),
                pedidoGuardado.getFechaCreacion()
        ));

        return pedidoGuardado;
    }

    // Avanzar al siguiente estado: RECIBIDO -> EN_PREPARACION -> LISTO -> ENTREGADO
    @Transactional
    public Pedido avanzarEstado(Long id) {

        Pedido pedido = buscarPedido(id);
        EstadoPedido estadoAnterior = pedido.getEstado();

        pedido.avanzarEstado();

        eventPublisher.publishEvent(new PedidoEstadoCambiadoEvento(
                pedido.getId(),
                pedido.getClienteId(),
                pedido.getRestauranteId(),
                estadoAnterior.name(),
                pedido.getEstado().name(),
                pedido.getFechaActualizacion()
        ));

        return pedido;
    }

    // Cancelar el pedido (cliente o restaurante)
    @Transactional
    public Pedido cancelar(Long id, CancelarPedidoRequestDTO solicitud) {

        Pedido pedido = buscarPedido(id);
        EstadoPedido estadoAnterior = pedido.getEstado();

        pedido.cancelar(solicitud.getMotivo());

        eventPublisher.publishEvent(new PedidoCanceladoEvento(
                pedido.getId(),
                pedido.getClienteId(),
                pedido.getRestauranteId(),
                estadoAnterior.name(),
                solicitud.getCanceladoPor().name(),
                solicitud.getMotivo(),
                itemsEvento(pedido),
                pedido.getFechaActualizacion()
        ));

        return pedido;
    }

    // Consultar un pedido por su identificador
    @Transactional(readOnly = true)
    public Pedido buscarPedido(Long id) {

        return pedidoRepository.buscarPorIdConDetalles(id)
                .orElseThrow(() -> new PedidoNoEncontradoException(
                        "No existe un pedido con el ID: " + id
                ));
    }

    // Listar pedidos por cliente o por restaurante (opcionalmente por estado)
    @Transactional(readOnly = true)
    public List<Pedido> listar(
            Long clienteId,
            Long restauranteId,
            EstadoPedido estado) {

        if (clienteId != null) {
            return pedidoRepository
                    .findByClienteIdOrderByFechaCreacionDesc(clienteId);
        }

        if (restauranteId != null && estado != null) {
            return pedidoRepository
                    .findByRestauranteIdAndEstadoOrderByFechaCreacionAsc(
                            restauranteId, estado);
        }

        if (restauranteId != null) {
            return pedidoRepository
                    .findByRestauranteIdOrderByFechaCreacionAsc(restauranteId);
        }

        throw new IllegalArgumentException(
                "Debe indicar clienteId o restauranteId");
    }

    private void validarRestaurante(MenuResponseDTO menu, Long restauranteId) {

        if (menu == null
                || menu.getRestaurante() == null
                || !Boolean.TRUE.equals(menu.getRestaurante().getActivo())
                || !menu.getRestaurante().getId().equals(restauranteId)) {

            throw new ReglaNegocioException(
                    "El restaurante no existe o no está disponible"
            );
        }

        if (!RESTAURANTE_ABIERTO.equals(
                menu.getRestaurante().getEstadoOperativo())) {

            throw new ReglaNegocioException(
                    "El restaurante está cerrado y no recibe pedidos"
            );
        }
    }

    private List<MenuExtraDTO> validarExtras(
            MenuPlatoDTO plato,
            List<Long> extrasIds) {

        List<MenuExtraDTO> extrasSeleccionados = new ArrayList<>();

        if (extrasIds == null) {
            return extrasSeleccionados;
        }

        Set<Long> extrasUnicos = new HashSet<>();

        for (Long extraId : extrasIds) {

            if (!extrasUnicos.add(extraId)) {
                throw new IllegalArgumentException(
                        "No se permite repetir el mismo extra"
                );
            }

            extrasSeleccionados.add(menuService.buscarExtra(plato, extraId));
        }

        return extrasSeleccionados;
    }

    private List<ItemEvento> itemsEvento(Pedido pedido) {

        return pedido.getItems()
                .stream()
                .map(item -> new ItemEvento(
                        item.getPlatoId(),
                        item.getNombrePlato(),
                        item.getCantidad()))
                .toList();
    }
}
