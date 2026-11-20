# ADR-002: Java 21, Spring Boot y Maven como base tecnológica

- **Fecha de la decisión:** 2026-11-14
- **Estado:** Aceptado
- **Evidencia:** commit `e470f58` (`pom.xml` de los tres servicios con `java.version=21` y `spring-boot-starter-parent` 4.1.2)
- **Registrado en el repositorio:** 2026-11-19

## Contexto
Necesitamos construir APIs REST, persistencia relacional, validación, mensajería AMQP, WebSocket y health checks en tres servicios, aplicando patrones GoF y principios SOLID, que es el foco del curso.

## Decisión
Usamos Java 21 (LTS) con Spring Boot 4.1.2 y Maven (con `mvnw` incluido) en los tres microservicios.

## Opciones evaluadas
- **A. Java + Spring Boot (elegida):** starters para JPA, AMQP, WebSocket, cache y actuator; inyección de dependencias que facilita DIP; el equipo ya conoce Java del curso.
- **B. Node.js + NestJS:** buen ecosistema para APIs, pero el curso evalúa patrones de diseño en Java y el equipo tiene menos experiencia.
- **C. Quarkus/Micronaut:** arranque más rápido, pero menos documentación y experiencia previa en el equipo.
- **Versión de Java:** se eligió 21 sobre 17 por ser la LTS vigente y coincidir con las imágenes `eclipse-temurin:21` usadas en Docker.
- **Maven vs Gradle:** se eligió Maven por su convención más simple y porque el wrapper permite compilar sin instalarlo.

## Consecuencias
- (+) Base común en los tres servicios y mucho soporte del framework.
- (+) Sin necesidad de instalar Maven localmente (`./mvnw`).
- (−) Spring Boot 4 es una versión mayor reciente: muchos ejemplos en línea corresponden a versiones anteriores.
- (−) Imágenes y consumo de memoria mayores que otras opciones más ligeras.
