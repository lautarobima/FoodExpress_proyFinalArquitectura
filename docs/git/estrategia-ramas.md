# Estrategia de ramas y trabajo colaborativo con Git
 
## 1. Estrategia utilizada
 
Durante el desarrollo de FoodExpress se utilizó una estrategia basada en una rama principal (`main`) y una rama independiente para el desarrollo del frontend (`front-end-branch`).
 
El backend se desarrolló directamente sobre `main`, donde se realizaron los commits de la implementación de los servicios, la configuración de RabbitMQ, CORS y mejoras generales.
 
Posteriormente, se creó `front-end-branch` para desarrollar el frontend y otros ajustes del proyecto, manteniendo esos cambios separados hasta su integración en `main` mediante el Pull Request #1.
 
Para el trabajo posterior se adoptó **GitHub Flow** (ver [ADR-011](../adr/ADR-011-estrategia-ramas-git.md)).
 
## 2. Estructura de ramas
 
- **main:** rama principal y estable. Contiene el backend, la estructura inicial, la documentación y, desde el PR #1, el frontend.
- **front-end-branch:** rama creada a partir de `main` (commit `8aafdf4`) con cuatro commits: el frontend y la documentación. Se integró a `main` con el Pull Request #1 el 18 de noviembre de 2026.
```
main:              e470f58 ─ cb08de8 ─ 9ffb6bc ─ bfe34c7 ─ 8aafdf4 ─────────────────────────────── 245514f ─ 9ce420c
                                                              \                                    /
front-end-branch:                                              c220dc7 ─ 79b756c ─ 421990c ─ 9d7f0e9
                                                                                         (PR #1)
```
 
## 3. Historial de desarrollo
 
| Fecha | Hash | Rama | Autor | Descripción |
|---|---|---|---|---|
| 2026-11-14 | e470f58 | main | Laura Catalina Hernández Rodríguez | Estructura inicial del proyecto |
| 2026-11-15 | cb08de8 | main | bedoya1905 | Integración de RabbitMQ y otros componentes |
| 2026-11-16 | 9ffb6bc | main | bedoya1905 | Configuración CORS |
| 2026-11-18 | bfe34c7 | main | bedoya1905 | Mejoras generales |
| 2026-11-18 | 8aafdf4 | main | bedoya1905 | Más mejoras |
| 2026-11-18 | c220dc7 | front-end-branch | lautarobima | backpart1 |
| 2026-11-18 | 79b756c | front-end-branch | lautarobima | backpart2 |
| 2026-11-18 | 421990c | front-end-branch | lautarobima | Incorporación de archivos (frontend) |
| 2026-11-18 | 9d7f0e9 | front-end-branch | Laura Catalina Hernández Rodríguez | Documentación terminada |
| 2026-11-18 | 245514f | main | Bedoya1905 | Merge del Pull Request #1 (`front-end-branch` → `main`) |
| 2026-11-19 | 9ce420c | main | Laura Catalina Hernández Rodríguez | Actualización de documentación (ADR-011 a ADR-014, plan de pruebas) |
 
## 4. Flujo de integración
 
La rama `front-end-branch` partió del commit `8aafdf4` de `main` y acumuló cuatro commits. Se integró a `main` mediante el **Pull Request #1**, creado por lautarobima y fusionado por bedoya1905 tras verificar que el proyecto levantaba con `docker compose up --build`.
 
Al integrar el frontend aparecieron los problemas de CORS y de arranque de nginx, que se resolvieron con nginx como reverse proxy y `depends_on` con `condition: service_healthy`.
 
## 5. Estrategia adoptada para el trabajo restante (GitHub Flow)
 
- `main` es la rama principal y siempre debe estar estable.
- Cada cambio se hace en una rama corta que sale de `main`: `feature/…`, `fix/…`, `docs/…` (por ejemplo `feature/RF-05-crear-pedido`, `fix/cors-frontend`, `docs/adr-fechas`).
- Los cambios se integran mediante Pull Request, con revisión y aprobación de al menos un integrante distinto al autor.
- Criterios de aprobación: `docker compose up --build` levanta los 6 contenedores en estado *healthy*, `scripts/probar-flujo.sh` pasa y no hay credenciales en el código.
Se descartó GitFlow porque las ramas `develop`, `release` y `hotfix` agregan ceremonia sin versiones paralelas que mantener, y trunk-based porque exige CI y pruebas automáticas que el proyecto aún no tiene (ADR-011).
 
## 6. Convenciones de commits
 
Se adoptó Conventional Commits (`tipo(alcance): descripción`), por ejemplo `feat(pedidos): agregar patrón State`:
 
- `feat`: nuevas funcionalidades.
- `fix`: correcciones.
- `docs`: documentación.
- `refactor`: reorganización del código.
- `test`: pruebas.
- `chore`: mantenimiento.
Varios commits del historial (`Mas mejoras`, `backpart1`, `backpart2`) no siguen esta convención porque se hicieron antes de adoptarla.
 
## 7. Coordinación del equipo
 
La coordinación y el reparto de tareas se hicieron por un grupo de WhatsApp. No se usó un gestor de tareas (GitHub Issues, Trello o Jira), por lo que los commits no están vinculados a issues.
 
## 8. Evidencias
 
Se anexan en el documento de entrega final las capturas del historial de commits, de las ramas del repositorio (*Insights → Network*) y del Pull Request #1.
 