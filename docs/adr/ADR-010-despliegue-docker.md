# ADR-010: Contenerización con Docker y Docker Compose

- **Fecha de la decisión:** 2026-11-15
- **Estado:** Aceptado
- **Evidencia:** commit `cb08de8` (primer `docker-compose.yml`), `8aafdf4` (Dockerfiles multi-stage y health checks), `421990c` (frontend + nginx)
- **Registrado en el repositorio:** 2026-11-19

## Contexto
El sistema tiene 6 componentes (frontend, 3 microservicios, PostgreSQL y RabbitMQ) y cualquier persona debe poder levantarlo con un solo comando, sin instalar Java, Node, PostgreSQL ni RabbitMQ.

## Decisión
Un `Dockerfile` multi-stage por servicio (build con `maven:3.9-eclipse-temurin-21`, ejecución con `eclipse-temurin:21-jre` y usuario no root) y un `docker-compose.yml` con redes propias, volúmenes persistentes, variables en `.env`, health checks y `depends_on: service_healthy`.

## Opciones evaluadas
- **A. Instalación manual en cada máquina:** no reproducible y propensa a diferencias de versión.
- **B. Imágenes y `docker run` a mano:** reproducible pero largo y fácil de equivocar.
- **C. Docker Compose (elegida):** entorno completo con `docker compose up`.
- **D. Kubernetes / Minikube:** orquestación real, pero desproporcionada para este alcance.

## Consecuencias
- (+) Arranque en un comando; la imagen final usa solo JRE y es más liviana.
- (+) Credenciales fuera del repositorio (`.env` ignorado, `.env.example` versionado).
- (−) `curl` se instala en cada imagen de runtime solo para el health check.
- (−) Los tests de contexto de Spring necesitan PostgreSQL y RabbitMQ, por lo que el build usa `-DskipTests` (ver ADR-014).
- (−) Compose sirve para desarrollo y demostración; no cubre despliegue en producción (réplicas, secretos, TLS).
