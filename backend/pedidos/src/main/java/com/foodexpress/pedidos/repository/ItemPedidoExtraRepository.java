package com.foodexpress.pedidos.repository;

import com.foodexpress.pedidos.model.entity.ItemPedidoExtra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoExtraRepository extends JpaRepository<ItemPedidoExtra, Long> {

    List<ItemPedidoExtra> findByItemPedidoId(Long itemPedidoId);

}