# Stack tecnológico (versiones)

Versiones tomadas de los `pom.xml`, `package-lock.json`, `Dockerfile` y `docker-compose.yml` del repositorio.

## Backend (los tres microservicios)

| Herramienta | Versión | Uso |
|---|---|---|
| Java (JDK) | 21 (LTS) | Lenguaje; imagen de build `maven:3.9-eclipse-temurin-21` |
| Spring Boot | 4.1.2 | Framework base (`spring-boot-starter-parent`) |
| Maven | 3.9 en la imagen de build; Maven Wrapper 3.3.4 (distribución 3.10.0) en local | Compilación y empaquetado |
| Spring Data JPA / Hibernate | Gestionada por Spring Boot 4.1.2 | Persistencia |
| Spring Validation | Gestionada por Spring Boot 4.1.2 | Validación de solicitudes |
| Spring Web MVC | Gestionada por Spring Boot 4.1.2 | API REST |
| Spring RestClient (`starter-restclient`) | Gestionada por Spring Boot 4.1.2 | Cliente HTTP de Pedidos hacia Restaurantes y Menú |
| Spring AMQP (`starter-amqp`) | Gestionada por Spring Boot 4.1.2 | Cliente de RabbitMQ |
| Spring Cache (`starter-cache`) | Gestionada por Spring Boot 4.1.2 | Caché del menú (solo Restaurantes y Menú) |
| Spring WebSocket (STOMP) | Gestionada por Spring Boot 4.1.2 | Tiempo real (solo Notificaciones) |
| Spring Boot Actuator | Gestionada por Spring Boot 4.1.2 | `/actuator/health` |
| Driver JDBC de PostgreSQL | Gestionada por Spring Boot 4.1.2 | Conexión a la base |
| JUnit (vía starters de test) | Gestionada por Spring Boot 4.1.2 | Pruebas unitarias |

## Infraestructura

| Herramienta | Versión / imagen | Uso |
|---|---|---|
| PostgreSQL | 16 (`postgres:16`) | Tres bases de datos, una por servicio |
| RabbitMQ | 4 (`rabbitmq:4-management`) | Mensajería asíncrona y panel de administración |
| Docker / Docker Compose | Docker Desktop + Compose v2 (`docker compose`) | Contenerización y orquestación |
| Imagen de ejecución Java | `eclipse-temurin:21-jre` | Runtime de los servicios (usuario no root) |

## Frontend

| Herramienta | Versión | Uso |
|---|---|---|
| React / React DOM | 18.3.1 | Interfaz de usuario |
| React Router DOM | 6.30.6 | Navegación |
| @stomp/stompjs | 7.5.0 | Cliente WebSocket (STOMP) |
| Vite | 5.4.21 | Build y servidor de desarrollo |
| @vitejs/plugin-react | 4.7.0 | Integración de React con Vite |
| Node.js | 22 (`node:22-alpine`, solo en la etapa de build) | Compilación del frontend |
| nginx | 1.27 (`nginx:1.27-alpine`) | Sirve la web y hace de proxy inverso hacia los servicios |

## Herramientas de proceso

| Herramienta | Uso |
|---|---|
| Git y GitHub | Control de versiones, Pull Requests e Issues |
| Postman | Pruebas funcionales de las APIs |
| DBeaver / IntelliJ | Inspección de las bases de datos en desarrollo |
| Herramienta de diagramas C4 [CONFIRMAR cuál usaron] | Diagramas de contexto y de contenedores |
