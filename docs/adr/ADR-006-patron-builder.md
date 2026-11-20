# ADR-006: Patrón Builder para construir pedidos

- **Fecha de la decisión:** 2026-11-14
- **Estado:** Aceptado
- **Evidencia:** commit `e470f58` (`patterns/builder/PedidoBuilder.java`, usado en `PedidoService.crearPedido`)
- **Registrado en el repositorio:** 2026-11-19

## Contexto
Un pedido tiene varios ítems, cada uno con cantidad, notas y extras opcionales, y las relaciones son bidireccionales (`Pedido` ↔ `ItemPedido` ↔ `ItemPedidoExtra`). Armarlo con setters sueltos facilita olvidar enlazar la relación inversa.

## Decisión
`PedidoBuilder` arma el pedido paso a paso (`conCliente`, `conRestaurante`, `agregarItem`, `agregarExtra`, `conValores`) y `build()` entrega el objeto completo.

## Opciones evaluadas
- **A. Constructor con muchos parámetros:** ilegible con listas anidadas.
- **B. Setters directos en el servicio:** flexible, pero repite el enlace entre pedido, ítem y extra, y permite objetos a medio construir.
- **C. Builder GoF (elegida):** encapsula el armado y los enlaces; el catálogo lo sugiere para este dominio.
- **D. `@Builder` de Lombok:** menos código, pero no maneja el enlace bidireccional y añade una dependencia.

## Consecuencias
- (+) `PedidoService` queda más legible y el enlace entre entidades no se olvida.
- (+) Agregar un atributo nuevo no cambia las firmas existentes.
- (−) `PedidoDirector` existe pero hoy no está en el flujo principal (el servicio usa el builder directamente); es candidato a eliminarse o a usarse en pruebas.
- (−) Las validaciones siguen en el servicio, no en `build()`.
