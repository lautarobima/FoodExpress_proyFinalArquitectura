
package com.foodexpress.restaurantes_menu.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodexpress.restaurantes_menu.dto.CategoriaRequestDTO;
import com.foodexpress.restaurantes_menu.dto.CategoriaResponseDTO;
import com.foodexpress.restaurantes_menu.exception.BusinessConflictException;
import com.foodexpress.restaurantes_menu.exception.ResourceNotFoundException;
import com.foodexpress.restaurantes_menu.model.entity.Categoria;
import com.foodexpress.restaurantes_menu.model.entity.Restaurante;
import com.foodexpress.restaurantes_menu.repository.CategoriaRepository;
import com.foodexpress.restaurantes_menu.repository.RestauranteRepository;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final RestauranteRepository restauranteRepository;

    public CategoriaService(
            CategoriaRepository categoriaRepository,
            RestauranteRepository restauranteRepository) {

        this.categoriaRepository = categoriaRepository;
        this.restauranteRepository = restauranteRepository;
    }

    // Crear una categoría para un restaurante
    @CacheEvict(value = MenuQueryService.CACHE_MENU, allEntries = true)
    public CategoriaResponseDTO crear(
            Long restauranteId,
            CategoriaRequestDTO dto) {

        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurante no encontrado"));

        if (!restaurante.getActivo()) {
            throw new BusinessConflictException("El restaurante está desactivado");
        }

        if (categoriaRepository.existsByRestaurante_IdAndNombreIgnoreCase(
                restauranteId, dto.getNombre())) {

            throw new BusinessConflictException(
                    "Ya existe una categoría con ese nombre");
        }

        Categoria categoria = new Categoria();

        categoria.setRestaurante(restaurante);
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setActiva(true);

        Categoria guardada = categoriaRepository.save(categoria);

        return convertirAResponse(guardada);
    }

    // Consultar las categorías de un restaurante
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarPorRestaurante(Long restauranteId) {

        return categoriaRepository.findByRestaurante_Id(restauranteId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // Actualizar una categoría
    @CacheEvict(value = MenuQueryService.CACHE_MENU, allEntries = true)
public CategoriaResponseDTO actualizar(
        Long restauranteId,
        Long id,
        CategoriaRequestDTO dto) {

    Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Categoría no encontrada"));

    // Verificar que la categoría pertenezca al restaurante indicado
    if (!categoria.getRestaurante().getId().equals(restauranteId)) {
        throw new ResourceNotFoundException(
                "La categoría no pertenece a este restaurante");
    }

    if (categoriaRepository
            .existsByRestaurante_IdAndNombreIgnoreCaseAndIdNot(
                    restauranteId, dto.getNombre(), id)) {

        throw new BusinessConflictException(
                "Ya existe otra categoría con ese nombre");
    }

    categoria.setNombre(dto.getNombre());
    categoria.setDescripcion(dto.getDescripcion());

    Categoria actualizada = categoriaRepository.save(categoria);

    return convertirAResponse(actualizada);
}

    // Desactivar una categoría
    @CacheEvict(value = MenuQueryService.CACHE_MENU, allEntries = true)
public void desactivar(Long restauranteId, Long id) {

    Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Categoría no encontrada"));

    // Verificar que la categoría pertenezca al restaurante indicado
    if (!categoria.getRestaurante().getId().equals(restauranteId)) {
        throw new ResourceNotFoundException(
                "La categoría no pertenece a este restaurante");
    }

    categoria.setActiva(false);

    categoriaRepository.save(categoria);
}

    // Convertir entidad a DTO
    private CategoriaResponseDTO convertirAResponse(Categoria categoria) {

        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getRestaurante().getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getActiva()
        );
    }
}
