package com.foodexpress.pedidos.service.client;

import java.time.Duration;

import com.foodexpress.pedidos.dto.menu.MenuResponseDTO;
import com.foodexpress.pedidos.exception.ReglaNegocioException;
import com.foodexpress.pedidos.service.port.MenuPort;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

// Adaptador HTTP hacia el microservicio Restaurantes y Menú
@Component
public class MenuClient implements MenuPort {

    private final RestClient restClient;

    public MenuClient(
            RestClient.Builder builder,
            @Value("${restaurantes-menu.url}") String menuUrl) {

        // Timeouts para no dejar colgada la creación del pedido
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        this.restClient = builder
                .baseUrl(menuUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public MenuResponseDTO obtenerMenu(Long restauranteId) {

        try {
            return restClient
                    .get()
                    .uri("/api/restaurantes/{restauranteId}/menu", restauranteId)
                    .retrieve()
                    .body(MenuResponseDTO.class);

        } catch (HttpClientErrorException ex) {
            // 404 o 409 del otro servicio: restaurante inexistente o desactivado
            throw new ReglaNegocioException(
                    "El restaurante no existe o no está disponible");
        }
    }
}
