package com.foodexpress.restaurantes_menu.service;

import com.foodexpress.restaurantes_menu.dto.MenuResponseDTO;

/**
 * ISP: interfaz solo de consulta del menú. La usan el MenuController y,
 * vía HTTP, el microservicio Pedidos. No expone operaciones administrativas
 * (crear/editar platos, extras, categorías), que viven en sus propios servicios.
 */
public interface MenuQueryService {

    String CACHE_MENU = "menu";

    MenuResponseDTO consultarMenuCompleto(Long restauranteId);
}
