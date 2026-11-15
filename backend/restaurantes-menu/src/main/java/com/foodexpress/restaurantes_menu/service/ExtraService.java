
package com.foodexpress.restaurantes_menu.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodexpress.restaurantes_menu.dto.ExtraRequestDTO;
import com.foodexpress.restaurantes_menu.dto.ExtraResponseDTO;
import com.foodexpress.restaurantes_menu.exception.BusinessConflictException;
import com.foodexpress.restaurantes_menu.exception.ResourceNotFoundException;
import com.foodexpress.restaurantes_menu.model.entity.Extra;
import com.foodexpress.restaurantes_menu.model.entity.Restaurante;
import com.foodexpress.restaurantes_menu.repository.ExtraRepository;
import com.foodexpress.restaurantes_menu.repository.RestauranteRepository;

@Service
@Transactional
public class ExtraService {

    private final ExtraRepository extraRepository;
    private final RestauranteRepository restauranteRepository;

    public ExtraService(
            ExtraRepository extraRepository,
            RestauranteRepository restauranteRepository) {

        this.extraRepository = extraRepository;
        this.restauranteRepository = restauranteRepository;
    }

    // Registrar un extra
    @CacheEvict(value = MenuQueryService.CACHE_MENU, allEntries = true)
    public ExtraResponseDTO crear(
            Long restauranteId,
            ExtraRequestDTO dto) {

        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Restaurante no encontrado"));

        if (!restaurante.getActivo()) {
            throw new BusinessConflictException(
                    "No se pueden agregar extras a un restaurante desactivado");
        }

        if (extraRepository.existsByRestaurante_IdAndNombreIgnoreCase(
                restauranteId, dto.getNombre())) {

            throw new BusinessConflictException(
                    "Ya existe un extra con ese nombre en el restaurante");
        }

        Extra extra = new Extra();

        extra.setRestaurante(restaurante);
        extra.setNombre(dto.getNombre());
        extra.setDescripcion(dto.getDescripcion());
        extra.setTipo(dto.getTipo());
        extra.setPrecioAdicional(dto.getPrecioAdicional());
        extra.setDisponible(true);

        Extra guardado = extraRepository.save(extra);

        return convertirAResponse(guardado);
    }

    // Consultar los extras de un restaurante
    @Transactional(readOnly = true)
    public List<ExtraResponseDTO> listarPorRestaurante(Long restauranteId) {

        if (!restauranteRepository.existsById(restauranteId)) {
            throw new ResourceNotFoundException(
                    "Restaurante no encontrado");
        }

        return extraRepository.findByRestaurante_Id(restauranteId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // Consultar un extra por su ID
    // Consultar un extra por su ID
@Transactional(readOnly = true)
public ExtraResponseDTO buscarPorId(Long restauranteId, Long id) {

    Extra extra = extraRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Extra no encontrado"));

    if (!extra.getRestaurante().getId().equals(restauranteId)) {
        throw new ResourceNotFoundException(
                "El extra no pertenece a este restaurante");
    }

    return convertirAResponse(extra);
}

    // Actualizar un extra
    @CacheEvict(value = MenuQueryService.CACHE_MENU, allEntries = true)
public ExtraResponseDTO actualizar(
        Long restauranteId,
        Long id,
        ExtraRequestDTO dto) {

    Extra extra = extraRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Extra no encontrado"));

    if (!extra.getRestaurante().getId().equals(restauranteId)) {
        throw new ResourceNotFoundException(
                "El extra no pertenece a este restaurante");
    }

    if (extraRepository
            .existsByRestaurante_IdAndNombreIgnoreCaseAndIdNot(
                    restauranteId, dto.getNombre(), id)) {

        throw new BusinessConflictException(
                "Ya existe otro extra con ese nombre en el restaurante");
    }

    extra.setNombre(dto.getNombre());
    extra.setDescripcion(dto.getDescripcion());
    extra.setTipo(dto.getTipo());
    extra.setPrecioAdicional(dto.getPrecioAdicional());

    Extra actualizado = extraRepository.save(extra);

    return convertirAResponse(actualizado);
}

    // Cambiar disponibilidad
    @CacheEvict(value = MenuQueryService.CACHE_MENU, allEntries = true)
public ExtraResponseDTO cambiarDisponibilidad(
        Long restauranteId,
        Long id,
        Boolean disponible) {

    Extra extra = extraRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Extra no encontrado"));

    if (!extra.getRestaurante().getId().equals(restauranteId)) {
        throw new ResourceNotFoundException(
                "El extra no pertenece a este restaurante");
    }

    if (disponible == null) {
        throw new BusinessConflictException(
                "Debe indicar la disponibilidad del extra");
    }

    extra.setDisponible(disponible);

    Extra actualizado = extraRepository.save(extra);

    return convertirAResponse(actualizado);
}

    // Convertir entidad a DTO
    private ExtraResponseDTO convertirAResponse(Extra extra) {

        return new ExtraResponseDTO(
                extra.getId(),
                extra.getRestaurante().getId(),
                extra.getNombre(),
                extra.getDescripcion(),
                extra.getTipo(),
                extra.getPrecioAdicional(),
                extra.getDisponible()
        );
    }
}
