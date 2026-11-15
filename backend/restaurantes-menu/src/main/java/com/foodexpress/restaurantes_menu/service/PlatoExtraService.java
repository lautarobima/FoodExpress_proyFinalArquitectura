package com.foodexpress.restaurantes_menu.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodexpress.restaurantes_menu.dto.PlatoExtraResponseDTO;
import com.foodexpress.restaurantes_menu.exception.BusinessConflictException;
import com.foodexpress.restaurantes_menu.exception.ResourceNotFoundException;
import com.foodexpress.restaurantes_menu.model.entity.Extra;
import com.foodexpress.restaurantes_menu.model.entity.Plato;
import com.foodexpress.restaurantes_menu.model.entity.PlatoExtra;
import com.foodexpress.restaurantes_menu.model.entity.PlatoExtraId;
import com.foodexpress.restaurantes_menu.repository.ExtraRepository;
import com.foodexpress.restaurantes_menu.repository.PlatoExtraRepository;
import com.foodexpress.restaurantes_menu.repository.PlatoRepository;

@Service
public class PlatoExtraService {

    private final PlatoExtraRepository platoExtraRepository;
    private final PlatoRepository platoRepository;
    private final ExtraRepository extraRepository;

    public PlatoExtraService(
            PlatoExtraRepository platoExtraRepository,
            PlatoRepository platoRepository,
            ExtraRepository extraRepository
    ) {
        this.platoExtraRepository = platoExtraRepository;
        this.platoRepository = platoRepository;
        this.extraRepository = extraRepository;
    }

    // Asociar un extra a un plato
    @Transactional
    @CacheEvict(value = MenuQueryService.CACHE_MENU, allEntries = true)
    public PlatoExtraResponseDTO agregarExtra(Long platoId, Long extraId) {

        Plato plato = platoRepository.findById(platoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No se encontró el plato"));

        Extra extra = extraRepository.findById(extraId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No se encontró el extra"));

        // Verificar que el plato pertenezca a un restaurante activo
        if (!plato.getCategoria().getRestaurante().getActivo()) {
            throw new BusinessConflictException(
                    "No se pueden modificar platos de un restaurante inactivo");
        }

        // Verificar que el extra esté disponible
        if (!extra.getDisponible()) {
            throw new BusinessConflictException(
                    "No se puede asociar un extra que no está disponible");
        }

        // Obtener el restaurante del plato y del extra
        Long restauranteDelPlato =
                plato.getCategoria().getRestaurante().getId();

        Long restauranteDelExtra =
                extra.getRestaurante().getId();

        // Ambos deben pertenecer al mismo restaurante
        if (!restauranteDelPlato.equals(restauranteDelExtra)) {
            throw new BusinessConflictException(
                    "El plato y el extra deben pertenecer al mismo restaurante");
        }

        // Evitar asociaciones duplicadas
        if (platoExtraRepository.existsByPlato_IdAndExtra_Id(platoId, extraId)) {
            throw new BusinessConflictException(
                    "Este extra ya está asociado al plato");
        }

        // Crear la relación
        PlatoExtraId id = new PlatoExtraId(platoId, extraId);

        PlatoExtra platoExtra = new PlatoExtra();
        platoExtra.setId(id);
        platoExtra.setPlato(plato);
        platoExtra.setExtra(extra);

        platoExtraRepository.save(platoExtra);

        return convertirAResponse(platoExtra);
    }

    // Consultar los extras asociados a un plato
    @Transactional(readOnly = true)
    public List<PlatoExtraResponseDTO> listarExtrasPorPlato(Long platoId) {

        if (!platoRepository.existsById(platoId)) {
            throw new ResourceNotFoundException(
                    "No se encontró el plato");
        }

        return platoExtraRepository.findByPlato_Id(platoId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // Quitar un extra de un plato
    @Transactional
    @CacheEvict(value = MenuQueryService.CACHE_MENU, allEntries = true)
    public void quitarExtra(Long platoId, Long extraId) {

        PlatoExtraId id = new PlatoExtraId(platoId, extraId);

        PlatoExtra relacion = platoExtraRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe esa asociación entre el plato y el extra"));

        platoExtraRepository.delete(relacion);
    }

    // Convertir entidad a DTO
    private PlatoExtraResponseDTO convertirAResponse(PlatoExtra platoExtra) {

        return new PlatoExtraResponseDTO(
                platoExtra.getPlato().getId(),
                platoExtra.getPlato().getNombre(),
                platoExtra.getExtra().getId(),
                platoExtra.getExtra().getNombre()
        );
    }
}