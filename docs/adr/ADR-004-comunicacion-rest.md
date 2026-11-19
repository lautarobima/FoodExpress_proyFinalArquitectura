# ADR-004: Comunicación síncrona mediante API REST

- **Fecha:** 2026-11-18
- **Estado:** Propuesto

## Contexto

Al crear un pedido, el microservicio Pedidos necesita verificar que el restaurante exista, esté abierto y que los platos y extras seleccionados estén disponibles. También necesita consultar sus precios.

## Decisión

Se decidió implementar comunicación síncrona mediante HTTP y API REST entre Pedidos y Restaurantes y Menú.

Pedidos utiliza una abstracción `MenuPort` y una implementación HTTP `MenuClient` para consultar la información del menú.

## Alternativas consideradas

- Consultar directamente la base de datos de Restaurantes y Menú.
- Replicar toda la información del menú en Pedidos.
- Consultar el menú mediante una API REST.

## Consecuencias

- Pedidos obtiene información actualizada para validar las solicitudes.
- Se mantiene la independencia de las bases de datos.
- La creación de pedidos depende de la disponibilidad del servicio de menú durante la validación.
- Se requiere gestionar errores de comunicación y respuestas HTTP inesperadas.