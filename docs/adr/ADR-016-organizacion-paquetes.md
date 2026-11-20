# ADR-016: Organización de paquetes por capa dentro de cada microservicio

- **Fecha de la decisión:** 2026-11-14
- **Estado:** Aceptado
- **Evidencia:** commit `e470f58` (estructura `controller / service / repository / model / dto / mapper / exception / config`)
- **Registrado en el repositorio:** 2026-11-19

## Contexto
Cada microservicio ya delimita un dominio (menú, pedidos, notificaciones), por lo que el código de cada uno es relativamente pequeño. El equipo conoce la estructura típica de Spring Boot y necesitaba ubicar rápido el código de cada responsabilidad.

## Decisión
Dentro de cada servicio se organiza por capa técnica (`controller`, `service`, `repository`, `model.entity`, `model.enums`, `dto`, `mapper`, `exception`, `config`). Los patrones de diseño y la mensajería se aíslan en paquetes propios (`patterns/builder`, `patterns/state`, `adapter`, `messaging`).

## Opciones evaluadas
- **A. Por capa (elegida):** convención conocida de Spring; fácil de navegar en servicios pequeños.
- **B. Por funcionalidad (feature):** agrupa todo lo de un caso de uso; útil en servicios grandes, pero aquí la división en microservicios ya cumple ese papel.
- **C. Arquitectura hexagonal / Clean Architecture:** separa dominio de infraestructura, pero exige más clases y mapeos para un dominio de este tamaño.

## Consecuencias
- (+) Estructura predecible y fácil de recorrer en los tres servicios.
- (+) Los patrones quedan visibles y localizados (`patterns/`, `adapter/`).
- (−) Las entidades JPA son también el modelo de dominio, lo que acopla el dominio al framework.
- (−) No hay clases `UseCase`: la orquestación vive en los servicios. La inversión de dependencias se aplicó donde más importa (`MenuPort`, `CanalNotificacion`).
