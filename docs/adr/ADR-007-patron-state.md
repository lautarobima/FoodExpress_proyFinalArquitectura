# ADR-007: Patrón State para el ciclo de vida del pedido

- **Fecha de la decisión:** 2026-11-15
- **Estado:** Aceptado
- **Evidencia:** commit `cb08de8` (`patterns/state/*`, `EstadoPedidoStateTest`)
- **Registrado en el repositorio:** 2026-11-19

## Contexto
El pedido sigue RECIBIDO → EN_PREPARACION → LISTO → ENTREGADO y puede cancelarse solo antes de estar LISTO. Las transiciones inválidas deben rechazarse, y el catálogo pide poder agregar un estado `EN_CAMINO` sin tocar los demás.

## Decisión
Cada estado es una clase que implementa `EstadoPedidoState` (`avanzar()`, `cancelar()`, `esFinal()`). `Pedido` guarda el enum `EstadoPedido` en base de datos y obtiene el objeto de estado con `EstadoPedidoStateFactory.desde(estado)`.

## Opciones evaluadas
- **A. `switch` / `if` sobre el enum en `PedidoService`:** rápido, pero las reglas se dispersan y crecen con cada estado.
- **B. Spring StateMachine:** potente, pero añade una dependencia y configuración que el problema no necesita.
- **C. State con clases (elegida):** cada regla vive en su estado (OCP) y se prueba por separado.

## Consecuencias
- (+) Agregar `EN_CAMINO` es una clase nueva más una línea en la fábrica y en el enum.
- (+) Cubierto con 5 pruebas unitarias (flujo normal, cancelación, estados finales, transición inválida).
- (−) La lista de estados queda en dos sitios (enum y clases) que deben mantenerse sincronizados.
- (−) Las transiciones inválidas lanzan `TransicionEstadoInvalidaException` (ver ADR-012).
