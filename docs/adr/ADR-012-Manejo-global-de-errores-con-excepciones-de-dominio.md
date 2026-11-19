# ADR-012: Manejo global de errores con excepciones de dominio

- **Fecha:** 19-11-2026  |  **Estado:** Aceptado

## Contexto
Las APIs deben responder errores consistentes (validación, recurso inexistente, transición de estado inválida, regla de negocio) sin exponer trazas internas.

## Decisión
Cada servicio define excepciones de dominio (`PedidoNoEncontradoException`, `TransicionEstadoInvalidaException`, `ReglaNegocioException`, `ResourceNotFoundException`, `BusinessConflictException`) y un `@RestControllerAdvice` (`GlobalExceptionHandler`) que las traduce a códigos HTTP con un cuerpo `ApiErrorDTO`.

## Opciones evaluadas
- Bloques try/catch en cada controlador: duplicación y respuestas inconsistentes.
- Result types: más explícitos, pero poco idiomáticos en Spring MVC.
- Excepciones + handler global: centraliza el mapeo.

## Consecuencias
(+) Controladores limpios y respuestas uniformes. (−) Cada servicio mantiene su propio handler (duplicación leve entre servicios).