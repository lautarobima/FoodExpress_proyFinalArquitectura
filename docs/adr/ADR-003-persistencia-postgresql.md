# ADR-003: Persistencia con PostgreSQL y separación de bases de datos

- **Fecha:** 2026-11-18
- **Estado:** Propuesto

## Contexto

Cada microservicio necesita almacenar información relacionada con su responsabilidad. Es importante evitar que un servicio dependa directamente de las tablas internas de otro.

## Decisión

Se decidió utilizar PostgreSQL como sistema de gestión de bases de datos.

Se definieron tres bases de datos independientes:

- `foodexpress_restaurantes`
- `foodexpress_pedidos`
- `foodexpress_notificaciones`

Aunque las tres bases de datos se ejecutan en una misma instancia de PostgreSQL dentro de Docker, cada microservicio administra sus propios datos.

## Alternativas consideradas

- Utilizar una única base de datos compartida por todos los microservicios.
- Utilizar una base de datos independiente para cada microservicio.
- Utilizar diferentes motores de bases de datos.

## Consecuencias

- Se mantiene la separación lógica de los datos.
- Los servicios no necesitan establecer relaciones JPA entre entidades de diferentes microservicios.
- La información externa se consulta mediante interfaces de comunicación.
- La instancia de PostgreSQL sigue siendo un recurso compartido de infraestructura.