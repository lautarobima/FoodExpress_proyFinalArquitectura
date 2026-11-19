# ADR-010: Contenerización y orquestación local con Docker Compose

- **Fecha:** 2026-11-18
- **Estado:** Propuesto

## Contexto

FoodExpress está compuesto por un frontend, tres microservicios, PostgreSQL y RabbitMQ. Configurar y ejecutar cada componente manualmente puede dificultar la instalación y reproducción del entorno de desarrollo.

## Decisión

Se decidió utilizar Docker y Docker Compose para construir y ejecutar los componentes del sistema.

Se definieron contenedores independientes para frontend, Restaurantes y Menú, Pedidos, Notificaciones, PostgreSQL y RabbitMQ.

También se configuraron redes, volúmenes persistentes, variables de entorno, dependencias entre servicios y comprobaciones de salud.

## Alternativas consideradas

- Instalar y ejecutar cada componente directamente en el sistema operativo.
- Construir imágenes independientes y gestionar manualmente su ejecución.
- Utilizar Docker Compose para definir y ejecutar el entorno completo.

## Consecuencias

- Se facilita reproducir el entorno de ejecución.
- Se mantienen los componentes aislados en contenedores.
- Se facilita la configuración conjunta de los servicios y sus dependencias.
- Se requiere gestionar imágenes, redes, volúmenes, variables y dependencias de inicio.