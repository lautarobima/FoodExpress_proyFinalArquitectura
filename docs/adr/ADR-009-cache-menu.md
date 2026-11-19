# ADR-009: Uso de caché para las consultas del menú

- **Fecha:** 2026-11-18
- **Estado:** Propuesto

## Contexto

El menú de los restaurantes puede consultarse repetidamente desde el frontend y desde el microservicio Pedidos. Consultar y construir toda la información en cada solicitud puede incrementar el trabajo realizado por el servicio.

## Decisión

Se decidió implementar caché para la consulta del menú completo mediante las capacidades de caché de Spring.

Las operaciones que modifican restaurantes, categorías, platos, extras o sus asociaciones deben invalidar la información almacenada en caché.

## Alternativas consideradas

- Consultar y construir el menú en cada solicitud.
- Almacenar temporalmente el resultado de las consultas y actualizarlo cuando cambien los datos.

## Consecuencias

- Se reducen consultas y procesamiento repetido para solicitudes frecuentes.
- Se mejora el rendimiento potencial de las consultas del menú.
- Se requiere invalidar la caché después de modificaciones.
- Una invalidación incorrecta puede mostrar información desactualizada.