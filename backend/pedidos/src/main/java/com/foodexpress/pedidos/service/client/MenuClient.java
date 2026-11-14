
package com.foodexpress.pedidos.service.client;

import com.foodexpress.pedidos.dto.menu.MenuResponseDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MenuClient {

    private final RestClient restClient;

    public MenuClient(
            RestClient.Builder builder,
            @Value("${restaurantes-menu.url}") String menuUrl) {

        this.restClient = builder
                .baseUrl(menuUrl)
                .build();
    }

    public MenuResponseDTO obtenerMenu(Long restauranteId) {

        return restClient
                .get()
                .uri("/api/restaurantes/{restauranteId}/menu", restauranteId)
                .retrieve()
                .body(MenuResponseDTO.class);
    }
}
