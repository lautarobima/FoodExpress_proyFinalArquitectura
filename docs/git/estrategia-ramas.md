# Estrategia de ramas y trabajo colaborativo con Git

## 1. Estrategia utilizada

Durante el desarrollo de FoodExpress se utilizó una estrategia de trabajo basada en una rama principal (`main`) y una rama independiente para el desarrollo e integración del frontend (`front-end-branch`).

El backend se desarrolló inicialmente sobre `main`, donde los integrantes realizaron commits correspondientes a la implementación de los servicios, configuración de RabbitMQ, CORS y mejoras generales.

Posteriormente, se creó `front-end-branch` para incorporar los cambios relacionados con el frontend y otros ajustes del proyecto, manteniendo separados esos cambios antes de integrarlos a la rama principal.

## 2. Estructura de ramas

- **main:** rama principal que contiene el desarrollo integrado del backend y la estructura inicial del proyecto.
- **front-end-branch:** rama creada a partir de `main`, que contiene tres commits adicionales pendientes de integración en el momento del registro del historial.

## 3. Historial de desarrollo

El historial revisado contiene los siguientes commits:

| Hash | Autor | Descripción |
|---|---|---|
| e470f58 | Laura Catalina Hernández Rodríguez | Estructura inicial del proyecto |
| cb08de8 | bedoya1905 | Integración de RabbitMQ y otros componentes |
| 9ffb6bc | bedoya1905 | Configuración CORS |
| bfe34c7 | bedoya1905 | Mejoras generales |
| 8aafdf4 | bedoya1905 | Más mejoras |
| c220dc7 | lautarobima | backpart1 |
| 79b756c | lautarobima | backpart2 |
| 421990c | lautarobima | Incorporación de archivos |

## 4. Flujo de integración

La rama `front-end-branch` parte del último commit registrado en `main` (8aafdf4) y contiene tres commits adicionales.

La integración de estos cambios se realizará hacia `main`, previa verificación del funcionamiento del proyecto.

## 5. Estrategia propuesta para el trabajo restante

Para las siguientes modificaciones se propone utilizar GitHub Flow:

- Mantener `main` como rama principal y estable.
- Crear ramas independientes para nuevas funcionalidades, correcciones y documentación.
- Utilizar nombres descriptivos para las ramas.
- Realizar commits con mensajes claros.
- Integrar cambios mediante Pull Requests y revisión de otro integrante.

## 6. Convenciones de commits

Se propone utilizar Conventional Commits:

- `feat`: nuevas funcionalidades.
- `fix`: correcciones.
- `docs`: documentación.
- `refactor`: reorganización del código.
- `test`: pruebas.
- `chore`: mantenimiento.

## 7. Evidencias

Se anexarán capturas del historial de commits, ramas del repositorio, Pull Requests y revisiones realizadas durante la integración.