# ADR-001: Arquitectura basada en microservicios

- **Fecha:** 2026-11-18
- **Estado:** Propuesto

## Contexto

FoodExpress necesita administrar restaurantes y menús, procesar pedidos y generar notificaciones. Estas funcionalidades tienen responsabilidades diferentes y requieren evolucionar de manera independiente.

## Decisión

Se decidió implementar una arquitectura basada en tres microservicios:

1. Restaurantes y Menú.
2. Pedidos.
3. Notificaciones.

Cada microservicio administra su propia lógica de negocio y expone las operaciones necesarias para comunicarse con los demás componentes.

## Alternativas consideradas

- Arquitectura monolítica: concentrar todas las funcionalidades en una sola aplicación.
- Arquitectura basada en microservicios: separar las responsabilidades en servicios independientes.

## Consecuencias

- Se obtiene separación de responsabilidades y mayor independencia entre componentes.
- Cada microservicio puede evolucionar sin modificar directamente los demás.
- Se requiere gestionar la comunicación, integración y despliegue de varios servicios.
- Se incrementa la complejidad de monitoreo y configuración.