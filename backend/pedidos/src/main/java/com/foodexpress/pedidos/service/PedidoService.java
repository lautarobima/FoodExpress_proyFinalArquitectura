
package com.foodexpress.pedidos.service;

import com.foodexpress.pedidos.dto.ItemPedidoRequestDTO;
import com.foodexpress.pedidos.dto.PedidoRequestDTO;
import com.foodexpress.pedidos.dto.menu.MenuExtraDTO;
import com.foodexpress.pedidos.dto.menu.MenuPlatoDTO;
import com.foodexpress.pedidos.dto.menu.MenuResponseDTO;
import com.foodexpress.pedidos.exception.PedidoNoEncontradoException;
import com.foodexpress.pedidos.model.entity.Pedido;
import com.foodexpress.pedidos.model.enums.EstadoPedido;
import com.foodexpress.pedidos.patterns.builder.PedidoBuilder;
import com.foodexpress.pedidos.repository.PedidoRepository;

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

    private final PedidoRepository pedidoRepository;
    private final MenuService menuService;
    private final PedidoCalculoService calculoService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            MenuService menuService,
            PedidoCalculoService calculoService) {

        this.pedidoRepository = pedidoRepository;
        this.menuService = menuService;
        this.calculoService = calculoService;
    }

    @Transactional
    public Pedido crearPedido(PedidoRequestDTO solicitud) {

        // 1. Consultar el menú del restaurante
        MenuResponseDTO menu = menuService.consultarMenu(
                solicitud.getRestauranteId()
        );

        // 2. Validar que el restaurante esté activo
        if (menu == null
                || menu.getRestaurante() == null
                || !Boolean.TRUE.equals(menu.getRestaurante().getActivo())
                || !menu.getRestaurante().getId().equals(
                        solicitud.getRestauranteId())) {

            throw new IllegalArgumentException(
                    "El restaurante no existe o no está disponible"
            );
        }

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

            List<MenuExtraDTO> extrasSeleccionados = new ArrayList<>();
            Set<Long> extrasUnicos = new HashSet<>();

            // 5. Validar los extras seleccionados
            if (itemSolicitud.getExtrasIds() != null) {

                for (Long extraId : itemSolicitud.getExtrasIds()) {

                    if (!extrasUnicos.add(extraId)) {
                        throw new IllegalArgumentException(
                                "No se permite repetir el mismo extra"
                        );
                    }

                    MenuExtraDTO extra = menuService.buscarExtra(
                            plato,
                            extraId
                    );

                    extrasSeleccionados.add(extra);
                }
            }

            // 6. Calcular el subtotal del plato
            BigDecimal subtotalItem = calculoService.calcularSubtotalItem(
                    plato,
                    extrasSeleccionados,
                    itemSolicitud.getCantidad()
            );

            subtotalesItems.add(subtotalItem);

            // 7. Agregar el plato al Builder
            int indiceItem = subtotalesItems.size() - 1;

            builder.agregarItem(
                    plato.getId(),
                    plato.getNombre(),
                    plato.getPrecio(),
                    itemSolicitud.getCantidad(),
                    itemSolicitud.getNotas()
            );

            // 8. Agregar los extras al plato correspondiente
            for (MenuExtraDTO extra : extrasSeleccionados) {

                builder.agregarExtra(
                        indiceItem,
                        extra.getId(),
                        extra.getNombre(),
                        extra.getPrecioAdicional()
                );
            }

            // 9. Asignar el subtotal real del elemento
            builder.build().getItems()
                    .get(indiceItem)
                    .setSubtotal(subtotalItem);
        }

        // 10. Calcular los valores generales del pedido
        BigDecimal subtotal = calculoService.calcularSubtotalPedido(
                subtotalesItems
        );

        // La tasa queda en cero provisionalmente hasta definirla
        BigDecimal tasaImpuesto = BigDecimal.ZERO;

        BigDecimal impuestos = calculoService.calcularImpuestos(
                subtotal,
                tasaImpuesto
        );

        BigDecimal total = calculoService.calcularTotal(
                subtotal,
                impuestos
        );

        // 11. Asignar los valores y construir el pedido
        builder.conValores(subtotal, impuestos, total);

        Pedido pedido = builder.build();

        // 12. Guardar en PostgreSQL
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        return pedidoRepository.buscarPorIdConDetalles(pedidoGuardado.getId())
        .orElseThrow(() -> new PedidoNoEncontradoException(
                "No se pudo recuperar el pedido guardado"
        ));
    }

    // Consultar un pedido por su identificador
    
        @Transactional(readOnly = true)
        public Pedido buscarPedido(Long id) {

        return pedidoRepository.buscarPorIdConDetalles(id)
                .orElseThrow(() -> new PedidoNoEncontradoException(
                        "No existe un pedido con el ID: " + id
                ));
        }

}
