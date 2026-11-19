
package com.foodexpress.restaurantes_menu.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodexpress.restaurantes_menu.dto.CategoriaMenuDTO;
import com.foodexpress.restaurantes_menu.dto.ExtraMenuDTO;
import com.foodexpress.restaurantes_menu.dto.MenuResponseDTO;
import com.foodexpress.restaurantes_menu.dto.PlatoMenuDTO;
import com.foodexpress.restaurantes_menu.dto.RestauranteResponseDTO;
import com.foodexpress.restaurantes_menu.exception.BusinessConflictException;
import com.foodexpress.restaurantes_menu.exception.ResourceNotFoundException;
import com.foodexpress.restaurantes_menu.model.entity.Categoria;
import com.foodexpress.restaurantes_menu.model.entity.Extra;
import com.foodexpress.restaurantes_menu.model.entity.Plato;
import com.foodexpress.restaurantes_menu.model.entity.PlatoExtra;
import com.foodexpress.restaurantes_menu.model.entity.Restaurante;
import com.foodexpress.restaurantes_menu.repository.CategoriaRepository;
import com.foodexpress.restaurantes_menu.repository.PlatoExtraRepository;
import com.foodexpress.restaurantes_menu.repository.PlatoRepository;
import com.foodexpress.restaurantes_menu.repository.RestauranteRepository;

@Service
@Transactional(readOnly = true)
public class MenuService implements MenuQueryService {

    private final RestauranteRepository restauranteRepository;
    private final CategoriaRepository categoriaRepository;
    private final PlatoRepository platoRepository;
    private final PlatoExtraRepository platoExtraRepository;

    public MenuService(
            RestauranteRepository restauranteRepository,
            CategoriaRepository categoriaRepository,
            PlatoRepository platoRepository,
            PlatoExtraRepository platoExtraRepository) {

        this.restauranteRepository = restauranteRepository;
        this.categoriaRepository = categoriaRepository;
        this.platoRepository = platoRepository;
        this.platoExtraRepository = platoExtraRepository;
    }

    // Consultar el menú completo de un restaurante.
    // Se guarda en caché: la consulta del menú es la operación más frecuente
    // (horas pico). Se invalida cuando cambia algo del menú (@CacheEvict).
    @Override
    @Cacheable(value = CACHE_MENU, key = "#restauranteId")
    public MenuResponseDTO consultarMenuCompleto(Long restauranteId) {

        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Restaurante no encontrado"));

        if (!Boolean.TRUE.equals(restaurante.getActivo())) {
            throw new BusinessConflictException(
                    "No se puede consultar el menú de un restaurante desactivado");
        }

        // Obtener las categorías activas del restaurante
        List<CategoriaMenuDTO> categorias = categoriaRepository
                .findByRestaurante_Id(restauranteId)
                .stream()
                .filter(categoria -> Boolean.TRUE.equals(categoria.getActiva()))
                .map(this::convertirCategoria)
                .toList();

        RestauranteResponseDTO restauranteDTO = new RestauranteResponseDTO(
                restaurante.getId(),
                restaurante.getNombre(),
                restaurante.getDescripcion(),
                restaurante.getDireccion(),
                restaurante.getTelefono(),
                restaurante.getActivo(),
                restaurante.getEstadoOperativo(),
                restaurante.getCreadoEn()
        );

        return new MenuResponseDTO(restauranteDTO, categorias);
    }

    // Convertir una categoría con sus platos disponibles
    private CategoriaMenuDTO convertirCategoria(Categoria categoria) {

        List<PlatoMenuDTO> platos = platoRepository
                .findByCategoria_Id(categoria.getId())
                .stream()
                .filter(plato -> Boolean.TRUE.equals(plato.getDisponible()))
                .map(this::convertirPlato)
                .toList();

        return new CategoriaMenuDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                platos
        );
    }

    // Convertir un plato con sus extras disponibles
    private PlatoMenuDTO convertirPlato(Plato plato) {

        List<ExtraMenuDTO> extras = platoExtraRepository
                .findByPlato_Id(plato.getId())
                .stream()
                .map(PlatoExtra::getExtra)
                .filter(extra -> Boolean.TRUE.equals(extra.getDisponible()))
                .map(this::convertirExtra)
                .toList();

        return new PlatoMenuDTO(
                plato.getId(),
                plato.getNombre(),
                plato.getDescripcion(),
                plato.getPrecio(),
                extras
        );
    }

    // Convertir un extra a su DTO
    private ExtraMenuDTO convertirExtra(Extra extra) {

        return new ExtraMenuDTO(
                extra.getId(),
                extra.getNombre(),
                extra.getDescripcion(),
                extra.getTipo(),
                extra.getPrecioAdicional()
        );
    }
}
