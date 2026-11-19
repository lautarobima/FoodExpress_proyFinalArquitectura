# ADR-008: Uso del patrón Adapter para los canales de notificación

- **Fecha:** 2026-11-18
- **Estado:** Propuesto

## Contexto

El microservicio Notificaciones necesita comunicarse mediante diferentes canales, como correo electrónico y WebSocket. Cada canal puede tener una interfaz y una forma de funcionamiento diferente.

## Decisión

Se decidió utilizar el patrón Adapter para unificar la interacción con los canales de notificación mediante una abstracción común, como `CanalNotificacion`.

Cada implementación adapta el mecanismo específico de su canal a la interfaz utilizada por el servicio.

## Alternativas consideradas

- Implementar directamente la lógica de cada canal dentro del servicio principal.
- Utilizar una interfaz común y adaptadores para los distintos canales.

## Consecuencias

- Se desacopla la lógica principal de las implementaciones específicas.
- Se facilita incorporar nuevos canales de notificación.
- Los canales pueden evolucionar de forma independiente.
- Cada adaptador debe respetar el contrato definido por la interfaz común.