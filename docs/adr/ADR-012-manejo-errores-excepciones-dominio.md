# ADR-012: Manejo global de errores con excepciones de dominio

- **Fecha de la decisión:** 2026-11-14
- **Estado:** Aceptado
- **Evidencia:** commit `e470f58` (`GlobalExceptionHandler` y excepciones en `pedidos` y `restaurantes-menu`)
- **Registrado en el repositorio:** 2026-11-19

## Contexto
Las APIs deben responder errores consistentes (validación, recurso inexistente, transición de estado inválida, regla de negocio) sin exponer trazas internas, y el frontend necesita distinguir entre 400, 404 y 409.

## Decisión
Pedidos y Restaurantes y Menú definen excepciones de dominio (`PedidoNoEncontradoException`, `TransicionEstadoInvalidaException`, `ReglaNegocioException`, `ResourceNotFoundException`, `BusinessConflictException`) y un `@RestControllerAdvice` (`GlobalExceptionHandler`) que las traduce a códigos HTTP con un cuerpo `ApiErrorDTO`.

## Opciones evaluadas
- **A. try/catch en cada controlador:** duplicación y respuestas inconsistentes.
- **B. Result types (éxito o error como valor de retorno):** explícito, pero poco idiomático en Spring MVC y obliga a propagar resultados por todas las capas.
- **C. Excepciones + handler global (elegida):** los servicios lanzan excepciones de negocio y un solo lugar las traduce a HTTP.

## Consecuencias
- (+) Controladores limpios y respuestas de error uniformes.
- (−) Pedidos y Menú mantienen cada uno su propio handler (duplicación leve).
- (−) Notificaciones no expone errores de negocio por API, por lo que no tiene handler propio.
