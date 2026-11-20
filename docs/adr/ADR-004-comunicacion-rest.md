# ADR-004: Consulta síncrona del menú mediante REST (puerto `MenuPort`)

- **Fecha de la decisión:** 2026-11-14
- **Estado:** Aceptado
- **Evidencia:** commit `e470f58` (`MenuClient`, `MenuPort`, `MenuService` en Pedidos)
- **Registrado en el repositorio:** 2026-11-19

## Contexto
Al crear un pedido, Pedidos debe verificar que el restaurante esté abierto y que los platos y extras estén disponibles, y debe tomar los precios del servidor (no del cliente). El catálogo exige que un plato no disponible no pueda pedirse "en el momento de la orden".

## Decisión
Pedidos consulta el menú a Restaurantes y Menú por HTTP/REST (`RestClient`) a través de la interfaz `MenuPort`, implementada por `MenuClient`.

## Opciones evaluadas
- **A. Leer directamente la base de Restaurantes:** dato siempre fresco, pero rompe el aislamiento de datos del ADR-003.
- **B. Replicar el menú en Pedidos mediante eventos:** no depende de Menú en línea, pero el precio o la disponibilidad podrían estar desactualizados al validar.
- **C. Consulta REST síncrona (elegida):** dato vigente y aislamiento de bases.

## Consecuencias
- (+) Validación con información actual y precios tomados del servidor, no del cliente (se verifica con la prueba CAL-05, pendiente de ejecutar).
- (+) `MenuPort` aplica DIP: se puede sustituir por un mock en pruebas.
- (−) Si Restaurantes y Menú no responde, no se pueden crear pedidos. El sistema falla de forma segura: no registra pedidos sin validar (pruebas INT-02/CAL-13).
- (−) Acoplamiento temporal entre servicios. Mejora futura: timeouts más finos y circuit breaker (hoy solo hay `connection-timeout=2s`).
