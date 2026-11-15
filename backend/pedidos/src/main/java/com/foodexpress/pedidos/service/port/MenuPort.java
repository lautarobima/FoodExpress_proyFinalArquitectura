package com.foodexpress.pedidos.service.port;

import com.foodexpress.pedidos.dto.menu.MenuResponseDTO;

/**
 * DIP: la lógica de pedidos depende de esta abstracción y no de la
 * implementación HTTP concreta (MenuClient). Se puede reemplazar por un
 * mock en pruebas o por otra fuente (caché, mensajería) sin tocar el servicio.
 */
public interface MenuPort {

    MenuResponseDTO obtenerMenu(Long restauranteId);
}
