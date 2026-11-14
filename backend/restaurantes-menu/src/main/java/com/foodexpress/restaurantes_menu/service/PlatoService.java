
package com.foodexpress.restaurantes_menu.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodexpress.restaurantes_menu.dto.PlatoRequestDTO;
import com.foodexpress.restaurantes_menu.dto.PlatoResponseDTO;
import com.foodexpress.restaurantes_menu.exception.BusinessConflictException;
import com.foodexpress.restaurantes_menu.exception.ResourceNotFoundException;
import com.foodexpress.restaurantes_menu.model.entity.Categoria;
import com.foodexpress.restaurantes_menu.model.entity.Plato;
import com.foodexpress.restaurantes_menu.repository.CategoriaRepository;
import com.foodexpress.restaurantes_menu.repository.PlatoRepository;

@Service
@Transactional
public class PlatoService {

    private final PlatoRepository platoRepository;
    private final CategoriaRepository categoriaRepository;

    public PlatoService(
            PlatoRepository platoRepository,
            CategoriaRepository categoriaRepository) {

        this.platoRepository = platoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // Registrar un plato
    public PlatoResponseDTO crear(PlatoRequestDTO dto) {

        Categoria categoria = obtenerCategoriaValida(dto.getCategoriaId());

        if (dto.getPrecio().signum() < 0) {
            throw new BusinessConflictException(
                    "El precio del plato no puede ser negativo");
        }

        Plato plato = new Plato();

        plato.setCategoria(categoria);
        plato.setNombre(dto.getNombre());
        plato.setDescripcion(dto.getDescripcion());
        plato.setPrecio(dto.getPrecio());
        plato.setDisponible(true);

        Plato guardado = platoRepository.save(plato);

        return convertirAResponse(guardado);
    }

    // Consultar los platos de una categoría
    @Transactional(readOnly = true)
    public List<PlatoResponseDTO> listarPorCategoria(Long categoriaId) {

        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResourceNotFoundException(
                    "Categoría no encontrada");
        }

        return platoRepository.findByCategoria_Id(categoriaId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // Consultar un plato por su ID
    @Transactional(readOnly = true)
    public PlatoResponseDTO buscarPorId(Long id) {

        Plato plato = platoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Plato no encontrado"));

        return convertirAResponse(plato);
    }

    // Actualizar un plato
    public PlatoResponseDTO actualizar(
            Long id,
            PlatoRequestDTO dto) {

        Plato plato = platoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Plato no encontrado"));

        Categoria categoria = obtenerCategoriaValida(dto.getCategoriaId());

        if (dto.getPrecio().signum() < 0) {
            throw new BusinessConflictException(
                    "El precio del plato no puede ser negativo");
        }

        plato.setCategoria(categoria);
        plato.setNombre(dto.getNombre());
        plato.setDescripcion(dto.getDescripcion());
        plato.setPrecio(dto.getPrecio());

        Plato actualizado = platoRepository.save(plato);

        return convertirAResponse(actualizado);
    }

    // Cambiar la disponibilidad de un plato
    public PlatoResponseDTO cambiarDisponibilidad(
            Long id,
            Boolean disponible) {

        Plato plato = platoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Plato no encontrado"));

        if (disponible == null) {
            throw new BusinessConflictException(
                    "Debe indicar la disponibilidad del plato");
        }

        plato.setDisponible(disponible);

        Plato actualizado = platoRepository.save(plato);

        return convertirAResponse(actualizado);
    }

    // Verificar que la categoría exista y esté activa
    private Categoria obtenerCategoriaValida(Long categoriaId) {

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Categoría no encontrada"));

        if (!categoria.getActiva()) {
            throw new BusinessConflictException(
                    "No se pueden registrar platos en una categoría desactivada");
        }

        if (!categoria.getRestaurante().getActivo()) {
            throw new BusinessConflictException(
                    "El restaurante de la categoría está desactivado");
        }

        return categoria;
    }

    // Convertir entidad a DTO
    private PlatoResponseDTO convertirAResponse(Plato plato) {

        return new PlatoResponseDTO(
                plato.getId(),
                plato.getCategoria().getId(),
                plato.getCategoria().getNombre(),
                plato.getNombre(),
                plato.getDescripcion(),
                plato.getPrecio(),
                plato.getDisponible(),
                plato.getCreadoEn()
        );
    }
}
