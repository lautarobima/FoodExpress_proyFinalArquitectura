
package com.foodexpress.restaurantes_menu.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodexpress.restaurantes_menu.dto.RestauranteRequestDTO;
import com.foodexpress.restaurantes_menu.dto.RestauranteResponseDTO;
import com.foodexpress.restaurantes_menu.exception.ResourceNotFoundException;
import com.foodexpress.restaurantes_menu.model.entity.Restaurante;
import com.foodexpress.restaurantes_menu.model.enums.EstadoOperativo;
import com.foodexpress.restaurantes_menu.repository.RestauranteRepository;
@Service
@Transactional
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;

    public RestauranteService(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    // Registrar un restaurante
    public RestauranteResponseDTO crear(RestauranteRequestDTO dto) {

        Restaurante restaurante = new Restaurante();

        restaurante.setNombre(dto.getNombre());
        restaurante.setDescripcion(dto.getDescripcion());
        restaurante.setDireccion(dto.getDireccion());
        restaurante.setTelefono(dto.getTelefono());

        restaurante.setActivo(true);
        restaurante.setEstadoOperativo(EstadoOperativo.CERRADO);

        Restaurante guardado = restauranteRepository.save(restaurante);

        return convertirAResponse(guardado);
    }

    // Consultar todos los restaurantes activos
    @Transactional(readOnly = true)
    public List<RestauranteResponseDTO> listarActivos() {

        return restauranteRepository.findByActivoTrue()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // Consultar un restaurante por su ID
    @Transactional(readOnly = true)
    public RestauranteResponseDTO buscarPorId(Long id) {

        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Restaurante no encontrado"));
                return convertirAResponse(restaurante);
    }

    // Actualizar información del restaurante
    @CacheEvict(value = MenuQueryService.CACHE_MENU, allEntries = true)
    public RestauranteResponseDTO actualizar(
            Long id,
            RestauranteRequestDTO dto) {

        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurante no encontrado"));

        restaurante.setNombre(dto.getNombre());
        restaurante.setDescripcion(dto.getDescripcion());
        restaurante.setDireccion(dto.getDireccion());
        restaurante.setTelefono(dto.getTelefono());

        Restaurante actualizado = restauranteRepository.save(restaurante);

        return convertirAResponse(actualizado);
    }

    // Desactivar un restaurante
    @CacheEvict(value = MenuQueryService.CACHE_MENU, allEntries = true)
    public void desactivar(Long id) {

        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurante no encontrado"));

        restaurante.setActivo(false);

        restauranteRepository.save(restaurante);
    }

    // Abrir o cerrar el restaurante (solo los abiertos reciben pedidos)
    @CacheEvict(value = MenuQueryService.CACHE_MENU, allEntries = true)
    public RestauranteResponseDTO cambiarEstadoOperativo(
            Long id,
            EstadoOperativo estado) {

        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurante no encontrado"));

        restaurante.setEstadoOperativo(estado);

        return convertirAResponse(restauranteRepository.save(restaurante));
    }

    // Convertir entidad a DTO de respuesta
    private RestauranteResponseDTO convertirAResponse(Restaurante restaurante) {

        return new RestauranteResponseDTO(
                restaurante.getId(),
                restaurante.getNombre(),
                restaurante.getDescripcion(),
                restaurante.getDireccion(),
                restaurante.getTelefono(),
                restaurante.getActivo(),
                restaurante.getEstadoOperativo(),
                restaurante.getCreadoEn()
        );
    }
}
