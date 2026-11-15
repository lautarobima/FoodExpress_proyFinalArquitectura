package com.foodexpress.pedidos.repository;

import java.util.List;
import java.util.Optional;

import com.foodexpress.pedidos.model.entity.Pedido;
import com.foodexpress.pedidos.model.enums.EstadoPedido;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("""
            SELECT DISTINCT p
            FROM Pedido p
            LEFT JOIN FETCH p.items
            WHERE p.id = :id
            """)
    Optional<Pedido> buscarPorIdConDetalles(@Param("id") Long id);

    // Historial de pedidos de un cliente (más recientes primero)
    List<Pedido> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);

    // Pedidos de un restaurante, opcionalmente filtrados por estado
    // (más antiguos primero, útil para el panel de cocina)
    List<Pedido> findByRestauranteIdOrderByFechaCreacionAsc(Long restauranteId);

    List<Pedido> findByRestauranteIdAndEstadoOrderByFechaCreacionAsc(
            Long restauranteId,
            EstadoPedido estado);
}
