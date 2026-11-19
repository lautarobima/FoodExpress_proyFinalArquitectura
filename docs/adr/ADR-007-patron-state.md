# ADR-007: Uso del patrón State para gestionar los estados de pedidos

- **Fecha:** 2026-11-18
- **Estado:** Propuesto

## Contexto

Los pedidos tienen un ciclo de vida con diferentes estados y transiciones permitidas. Es necesario evitar cambios de estado inválidos y concentrar las reglas de transición.

## Decisión

Se decidió implementar el patrón State en el microservicio Pedidos.

Cada estado del pedido representa un comportamiento específico y determina cuáles transiciones están permitidas.

Los estados definidos son:

- RECIBIDO
- EN_PREPARACION
- LISTO
- ENTREGADO
- CANCELADO

## Alternativas consideradas

- Gestionar todas las transiciones mediante múltiples condicionales dentro de un único servicio.
- Implementar el patrón State mediante clases que representen cada estado.

## Consecuencias

- Las reglas de transición quedan organizadas por estado.
- Se facilita agregar nuevos estados o modificar comportamientos.
- Se reduce la concentración de condicionales en la lógica principal.
- Se deben mantener pruebas que verifiquen las transiciones válidas e inválidas.