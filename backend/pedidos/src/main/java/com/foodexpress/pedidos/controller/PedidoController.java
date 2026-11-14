
package com.foodexpress.pedidos.controller;

import com.foodexpress.pedidos.dto.PedidoRequestDTO;
import com.foodexpress.pedidos.dto.PedidoResponseDTO;
import com.foodexpress.pedidos.mapper.PedidoMapper;
import com.foodexpress.pedidos.model.entity.Pedido;
import com.foodexpress.pedidos.service.PedidoService;

import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        PedidoResponseDTO respuesta =
                pedidoMapper.convertirADTO(pedido);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    

    
    // Consultar un pedido por su ID
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<PedidoResponseDTO> consultarPedido(
            @PathVariable Long id) {

        Pedido pedido = pedidoService.buscarPedido(id);

        PedidoResponseDTO respuesta =
                pedidoMapper.convertirADTO(pedido);

        return ResponseEntity.ok(respuesta);
    }

}
