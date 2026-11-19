# ADR-002: Uso de Java 21 y Spring Boot

- **Fecha:** 2026-11-18
- **Estado:** Propuesto

## Contexto

Los microservicios necesitan una tecnología que facilite la construcción de APIs REST, la persistencia de datos, la validación de solicitudes y la integración con RabbitMQ.

## Decisión

Se decidió utilizar Java 21 y Spring Boot para implementar los tres microservicios del sistema.

Se utilizan dependencias de Spring para persistencia, validación, comunicación HTTP, mensajería y monitoreo, según las responsabilidades de cada servicio.

## Alternativas consideradas

- Implementar los servicios con otro lenguaje y framework.
- Utilizar Java con Spring Boot para aprovechar su ecosistema de desarrollo empresarial.

## Consecuencias

- Se facilita la implementación de APIs REST y la inyección de dependencias.
- Se dispone de integración con PostgreSQL y RabbitMQ.
- Los tres servicios mantienen una base tecnológica común.
- El equipo debe gestionar las dependencias y configuraciones propias de Spring Boot.