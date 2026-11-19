package com.foodexpress.pedidos.controller;

import java.util.List;

import com.foodexpress.pedidos.dto.CancelarPedidoRequestDTO;
import com.foodexpress.pedidos.dto.PedidoRequestDTO;
import com.foodexpress.pedidos.dto.PedidoResponseDTO;
import com.foodexpress.pedidos.mapper.PedidoMapper;
import com.foodexpress.pedidos.model.entity.Pedido;
import com.foodexpress.pedidos.model.enums.EstadoPedido;
import com.foodexpress.pedidos.service.PedidoService;

import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Los métodos que devuelven pedidos ya guardados usan @Transactional para
// que el mapper pueda cargar los extras (relación LAZY) al armar la respuesta.
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoMapper pedidoMapper;

    public PedidoController(
            PedidoService pedidoService,
            PedidoMapper pedidoMapper) {

        this.pedidoService = pedidoService;
        this.pedidoMapper = pedidoMapper;
    }

    // Crear un nuevo pedido
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crearPedido(
            @Valid @RequestBody PedidoRequestDTO solicitud) {

        Pedido pedido = pedidoService.crearPedido(solicitud);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedidoMapper.convertirADTO(pedido));
    }

    // Consultar un pedido por su ID
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<PedidoResponseDTO> consultarPedido(
            @PathVariable Long id) {

        Pedido pedido = pedidoService.buscarPedido(id);

        return ResponseEntity.ok(pedidoMapper.convertirADTO(pedido));
    }

    // Listar pedidos: ?clienteId=1  o  ?restauranteId=1&estado=RECIBIDO
    @GetMapping
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPedidos(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long restauranteId,
            @RequestParam(required = false) EstadoPedido estado) {

        return pedidoService.listar(clienteId, restauranteId, estado)
                .stream()
                .map(pedidoMapper::convertirADTO)
                .toList();
    }

    // Avanzar al siguiente estado permitido:
    // RECIBIDO -> EN_PREPARACION -> LISTO -> ENTREGADO
    @PatchMapping("/{id}/estado")
    @Transactional
    public ResponseEntity<PedidoResponseDTO> avanzarEstado(
            @PathVariable Long id) {

        Pedido pedido = pedidoService.avanzarEstado(id);

        return ResponseEntity.ok(pedidoMapper.convertirADTO(pedido));
    }

    // Cancelar el pedido (solo en RECIBIDO o EN_PREPARACION)
    @PostMapping("/{id}/cancelar")
    @Transactional
    public ResponseEntity<PedidoResponseDTO> cancelar(
            @PathVariable Long id,
            @Valid @RequestBody CancelarPedidoRequestDTO solicitud) {

        Pedido pedido = pedidoService.cancelar(id, solicitud);

        return ResponseEntity.ok(pedidoMapper.convertirADTO(pedido));
    }
}
