# ADR-009: Caché del menú con Spring Cache

- **Fecha de la decisión:** 2026-11-15
- **Estado:** Aceptado
- **Evidencia:** commit `cb08de8` (`@EnableCaching`, `@Cacheable` en `MenuService.consultarMenuCompleto`, `@CacheEvict` en los servicios de escritura)
- **Registrado en el repositorio:** 2026-11-19

## Contexto
El menú completo se consulta desde el frontend y desde Pedidos en cada orden, y armarlo implica varias consultas (restaurante, categorías, platos, extras). El catálogo pide que responda rápido en horas pico.

## Decisión
`consultarMenuCompleto` usa `@Cacheable` (clave: id del restaurante) y toda operación que cambie restaurantes, categorías, platos, extras, asociaciones o stock usa `@CacheEvict(allEntries = true)`.

## Opciones evaluadas
- **A. Sin caché:** siempre fresco, pero repite consultas en cada orden.
- **B. Caché local de Spring (elegida):** una anotación, sin infraestructura extra.
- **C. Redis:** compartido entre réplicas y con expiración, pero agrega otro contenedor para un solo servicio.

## Consecuencias
- (+) Las lecturas repetidas no tocan la base (prueba CAL-06).
- (+) Cualquier cambio invalida el menú, incluido el ajuste de stock por eventos (CAL-07).
- (−) La caché vive en memoria de cada instancia: con varias réplicas habría menús distintos. Redis sería el siguiente paso.
- (−) `allEntries = true` borra el menú de todos los restaurantes ante un cambio en uno; es simple pero poco fino.
- (−) No hay expiración por tiempo.
