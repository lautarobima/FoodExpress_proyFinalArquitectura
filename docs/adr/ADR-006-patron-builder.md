# ADR-006: Uso del patrón Builder para construir pedidos

- **Fecha:** 2026-11-18
- **Estado:** Propuesto

## Contexto

Un pedido puede contener varios platos, cantidades, extras y notas especiales. Construir el objeto directamente mediante un constructor con muchos parámetros puede dificultar su lectura y mantenimiento.

## Decisión

Se decidió implementar el patrón Builder en el microservicio Pedidos.

El Builder permite configurar progresivamente la información del pedido y sus elementos antes de construir el objeto final.

## Alternativas consideradas

- Utilizar constructores con numerosos parámetros.
- Crear objetos mediante setters.
- Utilizar el patrón Builder para organizar la construcción del pedido.

## Consecuencias

- La construcción de pedidos resulta más organizada y legible.
- Se facilita la incorporación de nuevos atributos.
- La lógica de construcción queda separada de otras responsabilidades.
- Se requiere mantener las validaciones necesarias antes de finalizar la construcción.