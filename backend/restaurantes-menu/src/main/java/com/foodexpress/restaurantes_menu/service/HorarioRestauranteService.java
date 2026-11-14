package com.foodexpress.restaurantes_menu.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodexpress.restaurantes_menu.dto.HorarioRequestDTO;
import com.foodexpress.restaurantes_menu.dto.HorarioResponseDTO;
import com.foodexpress.restaurantes_menu.exception.BusinessConflictException;
import com.foodexpress.restaurantes_menu.exception.ResourceNotFoundException;
import com.foodexpress.restaurantes_menu.model.entity.HorarioRestaurante;
import com.foodexpress.restaurantes_menu.model.entity.Restaurante;
import com.foodexpress.restaurantes_menu.model.enums.DiaSemana;
import com.foodexpress.restaurantes_menu.repository.HorarioRestauranteRepository;
import com.foodexpress.restaurantes_menu.repository.RestauranteRepository;

@Service
public class HorarioRestauranteService {

    private final HorarioRestauranteRepository horarioRepository;
    private final RestauranteRepository restauranteRepository;

    public HorarioRestauranteService(
            HorarioRestauranteRepository horarioRepository,
            RestauranteRepository restauranteRepository
    ) {
        this.horarioRepository = horarioRepository;
        this.restauranteRepository = restauranteRepository;
    }

    // Crear un horario para un restaurante
    @Transactional
    public HorarioResponseDTO crear(Long restauranteId, HorarioRequestDTO dto) {

        Restaurante restaurante = obtenerRestauranteActivo(restauranteId);

        validarHoras(dto.getApertura(), dto.getCierre());

        validarCruceDeHorarios(
                restauranteId,
                dto.getDiaSemana(),
                dto.getApertura(),
                dto.getCierre(),
                null
        );

        HorarioRestaurante horario = new HorarioRestaurante();

        horario.setRestaurante(restaurante);
        horario.setDiaSemana(dto.getDiaSemana());
        horario.setHoraApertura(dto.getApertura());
        horario.setHoraCierre(dto.getCierre());

        HorarioRestaurante guardado = horarioRepository.save(horario);

        return convertirAResponse(guardado);
    }

    // Listar todos los horarios de un restaurante
    @Transactional(readOnly = true)
    public List<HorarioResponseDTO> listarPorRestaurante(Long restauranteId) {

        if (!restauranteRepository.existsById(restauranteId)) {
            throw new ResourceNotFoundException(
                    "No se encontró el restaurante");
        }

        return horarioRepository.findByRestaurante_Id(restauranteId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

   // Actualizar un horario
@Transactional
public HorarioResponseDTO actualizar(
        Long restauranteId,
        Long horarioId,
        HorarioRequestDTO dto) {

    HorarioRestaurante horario = horarioRepository.findById(horarioId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "No se encontró el horario"));

    // Verificar que el horario pertenezca al restaurante indicado
    if (!horario.getRestaurante().getId().equals(restauranteId)) {
        throw new ResourceNotFoundException(
                "El horario no pertenece a este restaurante");
    }

    obtenerRestauranteActivo(restauranteId);

    validarHoras(dto.getApertura(), dto.getCierre());

    validarCruceDeHorarios(
            restauranteId,
            dto.getDiaSemana(),
            dto.getApertura(),
            dto.getCierre(),
            horarioId
    );

    horario.setDiaSemana(dto.getDiaSemana());
    horario.setHoraApertura(dto.getApertura());
    horario.setHoraCierre(dto.getCierre());

    HorarioRestaurante actualizado = horarioRepository.save(horario);

    return convertirAResponse(actualizado);
}

    // Eliminar un horario
@Transactional
public void eliminar(Long restauranteId, Long horarioId) {

    HorarioRestaurante horario = horarioRepository.findById(horarioId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "No se encontró el horario"));

    // Verificar que el horario pertenezca al restaurante indicado
    if (!horario.getRestaurante().getId().equals(restauranteId)) {
        throw new ResourceNotFoundException(
                "El horario no pertenece a este restaurante");
    }

    horarioRepository.delete(horario);
}

    // Buscar un restaurante y verificar que esté activo
    private Restaurante obtenerRestauranteActivo(Long restauranteId) {

        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró el restaurante"));

        if (!Boolean.TRUE.equals(restaurante.getActivo())) {
            throw new BusinessConflictException(
                    "No se pueden gestionar horarios de un restaurante inactivo");
        }

        return restaurante;
    }

    // Verificar que la apertura sea anterior al cierre
    private void validarHoras(LocalTime apertura, LocalTime cierre) {

        if (apertura == null || cierre == null) {
            throw new BusinessConflictException(
                    "Las horas de apertura y cierre son obligatorias");
        }

        if (!apertura.isBefore(cierre)) {
            throw new BusinessConflictException(
                    "La hora de apertura debe ser anterior a la hora de cierre");
        }
    }

    // Evitar horarios que se superpongan
    private void validarCruceDeHorarios(
            Long restauranteId,
            DiaSemana diaSemana,
            LocalTime nuevaApertura,
            LocalTime nuevoCierre,
            Long horarioExcluir
    ) {

        List<HorarioRestaurante> horarios =
                horarioRepository.findByRestaurante_IdAndDiaSemana(
                        restauranteId,
                        diaSemana
                );

        for (HorarioRestaurante existente : horarios) {

            // Si estamos actualizando, ignoramos el horario actual
            if (horarioExcluir != null
                    && horarioExcluir.equals(existente.getId())) {
                continue;
            }

            boolean seSuperpone =
                    nuevaApertura.isBefore(existente.getHoraCierre())
                    && nuevoCierre.isAfter(existente.getHoraApertura());

            if (seSuperpone) {
                throw new BusinessConflictException(
                        "El horario se superpone con otro intervalo registrado");
            }
        }
    }

    // Convertir entidad a DTO
    private HorarioResponseDTO convertirAResponse(
            HorarioRestaurante horario
    ) {

        return new HorarioResponseDTO(
                horario.getId(),
                horario.getRestaurante().getId(),
                horario.getDiaSemana(),
                horario.getHoraApertura(),
                horario.getHoraCierre()
        );
    }
}