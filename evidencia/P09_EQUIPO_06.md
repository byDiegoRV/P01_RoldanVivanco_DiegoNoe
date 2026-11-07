# P09 · Equipo 06 · API REST básica de ventas (M12)

Índice de evidencia mapeado a la rúbrica R01. Cada afirmación declara su estado por separado:
**VERIFICADO** (se ejecutó y hay evidencia), **NO_VERIFICADO** (no se pudo comprobar) o **PENDIENTE** (falta hacerlo).

- Repositorio: https://github.com/byDiegoRV/P01_RoldanVivanco_DiegoNoe
- Rama de entrega: `master`
- Tag: `v0.3-api` (PENDIENTE hasta crearlo; ver sección Git)
- Integrantes: Diego Noe Roldán Vivanco (integrante 1) y Xochitl Salgado (integrante 2)

## 1. Funcionamiento (0.8)

| Qué se afirma | Estado | Evidencia |
|---|---|---|
| `GET /api/productos` devuelve 200 con la lista | VERIFICADO | `evidencia/P09/01-listar.txt` |
| `POST /api/ventas` devuelve 201 con folio, total y partidas | VERIFICADO | `evidencia/P09/02-venta-201.txt` |
| La venta descuenta la existencia | VERIFICADO | `evidencia/P09/03-existencia-descontada.txt` |
| Validación: cantidad inválida devuelve 400 `VALIDACION_FALLIDA` | VERIFICADO | `evidencia/P09/04-validacion-400.txt` |
| Campo no permitido (p. ej. `tarjeta`) devuelve 400 y no se refleja | VERIFICADO | `evidencia/P09/05-campo-desconocido-400.txt` |
| Producto inexistente devuelve 404 `PRODUCTO_NO_ENCONTRADO` | VERIFICADO | `evidencia/P09/06-producto-404.txt`, `evidencia/P09/curl-p09.txt` |
| Sin existencia suficiente devuelve 409 `EXISTENCIA_INSUFICIENTE` | VERIFICADO | `evidencia/P09/07-sin-existencia-409.txt` |
| Id no numérico devuelve 400 `SOLICITUD_INVALIDA` | VERIFICADO | `evidencia/P09/08-id-invalido-400.txt` |
| `./scripts/verify-module.sh M12` termina con `MODULO_M12_VERIFICADO` | VERIFICADO | `evidencia/P09/xochitl-verify-m12.txt` (resumen sin trazas) |
| Reproducción desde un clon limpio en otro equipo | VERIFICADO | `evidencia/EVIDENCIA_XOCHITL/P09.md` |
| Demostración completa con `scripts/curl-p09.sh` | VERIFICADO | `scripts/curl-p09.sh`, `evidencia/P09/curl-p09.txt` |
| Un 500 real contra la base de datos | NO_VERIFICADO | Solo cubierto con prueba unitaria (el mensaje no filtra el detalle) |

## 2. Solución técnica (0.5)

- Arquitectura hexagonal conservada: puertos de entrada `ConsultarProductosUseCase` y `VenderProductosUseCase`, servicios en `application/service`, controladores y DTOs en `infrastructure/delivery/rest`.
- Los controladores solo traducen HTTP a casos de uso; no exponen entidades de dominio.
- Validación con Bean Validation; Jackson rechaza campos desconocidos (la API no acepta datos de tarjeta).
- Errores con un único formato JSON (`timestamp`, `status`, `code`, `message`, `path`, `details`) en `ManejadorErrores`; el 500 es genérico y no filtra detalles.
- Contrato OpenAPI 3.0.3 en `docs/openapi.yaml`.
- Spring Boot 2.7.18 sobre Java 11, sin importar el BOM para no alterar las versiones de Hibernate (decisión explicada en `README.md`).

| Qué se afirma | Estado |
|---|---|
| El contrato coincide con el comportamiento observado (rutas, códigos y cuerpos) | VERIFICADO contra las salidas de `evidencia/P09/` |
| El contrato valida sin errores en un editor OpenAPI | VERIFICADO en https://editor.swagger.io (`evidencia/P09/capturas/P04-contrato-swagger.png`) |

## 3. Pruebas (0.3)

- Total: **142 pruebas, 0 fallos** (`mvn clean test`). VERIFICADO.
- Casos positivos y negativos HTTP con MockMvc: `ProductosRestControllerTest`, `VentasRestControllerTest`, `ManejadorErroresTest`, `ProductosListaVaciaTest`.
- Servicios: `ConsultarProductosServiceTest`, `VenderProductosServiceTest`.
- Códigos cubiertos: 200, 201, 400 (validación, JSON mal formado, campo desconocido, id no numérico, sin cuerpo), 404 (producto y ruta), 405, 409, 415 y 500 genérico.
- Las pruebas de persistencia requieren PostgreSQL (`docker compose up -d`).

## 4. Calidad y Sonar (0.2)

| Qué se afirma | Estado | Evidencia |
|---|---|---|
| Análisis previo: 7 code smells en código nuevo (1 Blocker, 1 Critical, 5 Major), corregidos | VERIFICADO | capturas Q1 y Q2 en `evidencia/P09/capturas/` |
| Análisis tras las correcciones: Quality Gate Passed, 0 issues, cobertura de código nuevo 84.7 % | VERIFICADO (análisis del 31-oct-2026, 15:21) | capturas Q3 y Q4 |
| Análisis final (código global): Quality Gate Passed, 0 bugs, 0 vulnerabilidades, 0 hotspots, 0 code smells, cobertura 84.7 %, duplicación 0.0 %, 142 pruebas | VERIFICADO (7-nov-2026, 13:10) | `evidencia/P09/capturas/P07-Q3-sonar-despues.png` y `P07-Q4-issues-final.png` |

Alcance de la cobertura: `ConfiguracionApi` no tiene prueba unitaria por diseño (el perfil `test` la desactiva y se ejerce al arrancar la API); la interfaz Swing de C02 explica gran parte del resto. No se excluyeron archivos de la medición.

## 5. Git y evidencia (0.2)

| Elemento | Estado | Detalle |
|---|---|---|
| Issues #42 a #48 con commits `refs #n` | VERIFICADO | historial de `master` |
| PR #49 (Noe): base, endpoints, errores y contrato | VERIFICADO (fusionado) | cierra #42 a #45 |
| PR #50 (Xochitl): pruebas, guía y resumen de verify-module | VERIFICADO (fusionado) | cierra #46 y #47 |
| PR de cierre (Noe): índice, contribuciones y Sonar final | PENDIENTE | cierra #48 |
| Tag `v0.3-api` sobre `master` | PENDIENTE | crear tras fusionar todos los PR |
| Contribuciones individuales | VERIFICADO tras la fusión | `docs/contribuciones/integrante-1.md` y `integrante-2.md` |
| Registro de errores | VERIFICADO | `evidencia/EVIDENCIA_XOCHITL/P09.md` |

**Nota.** El script `scripts/curl-p09.sh` y la sección P09 del `README.md` (parte del #47) los completó la integrante 2 después de fusionado el PR #50, por lo que no entraron en él; se integran en el PR de cierre.

## 6. Cómo reproducir (resumen)

```bash
docker compose up -d
set -a; source .env; set +a
mvn clean test
./scripts/verify-module.sh M12
PORT=9098 mvn spring-boot:run        # en otra terminal: PORT=9098 ./scripts/curl-p09.sh
```
La guía completa está en `evidencia/EVIDENCIA_XOCHITL/P09.md` y en la sección P09 de `README.md`.

## 7. Restricciones declaradas

- No se guardan credenciales, datos personales ni trazas de pila en la evidencia.
- La API no acepta datos de tarjeta.
