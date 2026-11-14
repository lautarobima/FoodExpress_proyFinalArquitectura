
package com.foodexpress.pedidos.repository;

import com.foodexpress.pedidos.model.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("""
            SELECT DISTINCT p
            FROM Pedido p
            LEFT JOIN FETCH p.items
            WHERE p.id = :id
            """)
    Optional<Pedido> buscarPorIdConDetalles(@Param("id") Long id);
}
