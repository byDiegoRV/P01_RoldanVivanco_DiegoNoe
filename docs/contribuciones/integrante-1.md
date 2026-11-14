# Contribución P09 · Diego Noe Roldán Vivanco (integrante 1)

## Issues
- #42 P09-01: base Spring Boot y verificación M12.
- #43 P09-02: endpoints, DTOs y validación.
- #44 P09-03: errores consistentes.
- #45 P09-04: contrato OpenAPI.
- #48 P09-07: calidad, evidencia y cierre (con Xochitl).

## Qué hice
- Base Spring Boot 2.7.18 sobre Java 11, `ApiApplication` y `ConfiguracionApi`, y soporte de `M12` en `scripts/verify-module.sh`.
- Endpoints `GET /api/productos`, `GET /api/productos/{id}` y `POST /api/ventas` con DTOs, casos de uso y validación.
- `ManejadorErrores` con un único formato de error y los códigos 400, 404, 405, 409, 415 y 500.
- Contrato `docs/openapi.yaml`.
- Pruebas de servicio y de controladores (MockMvc), y corrección de los 7 hallazgos de Sonar.
- Evidencia de ejecución real contra PostgreSQL en `evidencia/P09/` e índice `evidencia/P09_EQUIPO_06.md`.
- Revisión del PR #50 de Xochitl.
- Integré en el PR de cierre el script `scripts/curl-p09.sh` y la sección P09 del README, que la integrante 2 completó después de fusionado el PR #50.


## Decisiones técnicas
- Spring Boot 2.7 en lugar de 3 por Java 11.
- Sin importar el BOM de Spring Boot, para no forzar versiones que rompen Hibernate 6.4.4.
- `ConfiguracionApi` con `@Profile("!test")` para que las pruebas HTTP no necesiten base de datos.


---

## P10 — Cliente Angular (M13)

**Integrante:** Diego Noe Roldán Vivanco (`byDiegoRV`)
**Rama:** `feature/p10-cliente-angular`
**Periodo:** 9 al 15 de noviembre de 2026

### Contribuciones

| Issue | Trabajo realizado | Resultado | Estado |
|---|---|---|---|
| #52 (P10-01) | Base Angular 20 con componentes standalone y proxy de desarrollo `/api` hacia `http://localhost:9098`, sin modificar la API ni habilitar CORS | `frontend/proxy.conf.json`, script `npm start` | VERIFICADO (`ng build`) |
| #53 (P10-02) | Modelos tipados que reflejan `docs/openapi.yaml`, `ProductosService`, `VentasService` y conversión de errores al formato de error de la API (`aErrorApi`) | Pruebas de servicio con `HttpTestingController` | VERIFICADO (`ng test`) |
| #54 (P10-03) | Catálogo de productos con estados de carga, vacío, error (con "Reintentar") y listado; diseño con la paleta institucional de la UV | Pruebas del componente; revisión en navegador | VERIFICADO (`ng test` y navegador) |
| #55 (P10-04) | Pantalla de venta: carrito, validación de cantidad contra existencia, pago simulado, registro con `POST /api/ventas`, folio y total, errores visibles y navegación | Pruebas del componente (casos positivos y de error); revisión en navegador | VERIFICADO (`ng test` y navegador) |
| P10-07 | Soporte de `M13` en `scripts/verify-module.sh` | `./scripts/verify-module.sh M13` | VERIFICADO |

### Decisiones técnicas

- **Proxy en lugar de CORS:** el cliente llama a rutas relativas `/api/...` y el servidor de desarrollo las reenvía a la API. Así el backend de P09 no cambia.
- **Errores:** todo error se convierte al formato único de la API (`status`, `code`, `message`, `details`). La falta de conexión y los errores inesperados generan un mensaje genérico propio del cliente, sin exponer detalles internos.
- **Pago simulado:** replica el paso de pago de la versión en Swing (nombre del titular y 16 dígitos). Los datos solo viven en memoria del componente: no se envían a la API, no se guardan y se borran al confirmar. El recibo muestra únicamente la terminación de la tarjeta. Las pruebas y capturas usan datos ficticios. La API de P09 no procesa pagos y no se modificó.
- **Diseño:** se usan los colores institucionales (azul `#18529D` y verde `#28AD56`) definidos como variables CSS. No se usan el escudo ni el logotipo de la UV.

### Evidencia

- `evidencia/P10/P10-04_build.txt` y `evidencia/P10/P10-04_test.txt`: salidas de `ng build` y `ng test`.
- `evidencia/P10/P10-07_verify-m13.txt`: salida de `./scripts/verify-module.sh M13` (se agrega al ejecutarlo).
- `evidencia/P10/capturas/`: catálogo, carrito, venta exitosa, cantidad inválida, tarjeta inválida y error sin conexión.
- `scripts/evidencia-p10.sh`: genera las salidas anteriores sin rutas personales.
- Calidad (backend): análisis de SonarQube ejecutado el 9 de noviembre de 2026 sobre el proyecto Java. Resultado: 84.7% y 0 Issues.

### Declaración de estado

- **VERIFICADO:** compilación (`ng build`), pruebas del cliente (`ng test`) y recorrido manual contra la API en el puerto 9098 (caso positivo, validación de cantidad, validación de pago y error sin conexión).
- **VERIFICADO:** compilación, pruebas del cliente y recorrido manual contra la API (puerto 9098). Cobertura medida con `ng test --code-coverage`: Statements X %, Branches X %, Functions X %, Lines X % (`evidencia/P10/P10-05_cobertura.txt`).
- **NO_VERIFICADO:** análisis de Sonar sobre el código TypeScript (el análisis cubre solo el backend, por la configuración del escáner y la versión de Node); pruebas de extremo a extremo automatizadas (no requeridas por la actividad).
- **VERIFICADO:** salida de `verify-module.sh M13`, guía de reproducción y registro de errores (a cargo de la otra integrante), pull request, tag o commit de entrega y entrega en Eminus.

## C03 — Integración web (Corte 3, R04)

**Integrante:** Diego Noe Roldán Vivanco (`byDiegoRV`)
**Rama:** `feature/c03-noe` (un PR para los issues #62, #63 y #64)

### Qué hice

| Issue | Trabajo | Archivos principales | Criterio R04 |
|---|---|---|---|
| #62 C03-01 | Reutilización del núcleo: la interfaz Swing deja de armar la venta y usa `VenderProductosUseCase`, el mismo caso de uso de la API | `VentasController.java`, `MainSwing.java`, `VentasControllerPersistenteTest.java`, `docs/reutilizacion-nucleo.md`, `scripts/evidencia-c03-dependencias.sh` | Reutilización del núcleo |
| #63 C03-02 | Navegación Angular: detalle de producto en `/productos/:id` (carga, éxito, 404 y error con reintento); validación del nombre del titular igual que Swing | `frontend/src/app/producto/`, `app.routes.ts`, `catalogo/`, `venta/venta.component.ts` y sus pruebas | Angular, Errores y validación |
| #64 C03-03 | Persistencia de extremo a extremo: venta por la API, consulta a PostgreSQL, tres casos de error y prueba tras reiniciar la API | `scripts/evidencia-c03-persistencia.sh` | API, Calidad (evidencia de persistencia) |

### Decisiones técnicas

- **Swing usa el caso de uso compartido.** `construirVenta()` repetía la orquestación de `VenderProductosService` (folio, lectura fresca de existencias, partidas y registro). Se eliminó y el controlador solo convierte el carrito en `ItemVenta`; así API y Swing recorren las mismas reglas del dominio.
- **Mensaje de producto inexistente.** Ahora lo produce el caso de uso compartido (`ProductoNoEncontradoException`) y es el mismo que ve la API; por eso se actualizó la aserción de una prueba de Swing.
- **Detalle de producto.** Lee el identificador de la ruta; un identificador no numérico o menor que 1 no consulta la API y se trata como producto inexistente. El 404 no ofrece reintentar; los errores de conexión sí.
- **Validación del nombre.** Angular usa la misma expresión regular y el mismo mensaje que Swing (solo letras, acentos, ñ y espacios). El pago sigue siendo una simulación de la interfaz y no se envía a la API.
- **Evidencia de base de datos sin credenciales.** Las consultas se ejecutan con `docker exec` dentro del contenedor usando solo `DB_USER` y `DB_NAME`; la contraseña no se imprime ni se guarda.

### Pruebas

- Backend: 142 pruebas, sin fallos (`mvn clean verify`).
- Angular: 34 pruebas (27 anteriores, 5 del detalle de producto y 2 de validación del nombre).

### Evidencia

- `evidencia/C03/C03-01_dependencias_antes.txt` y `C03-01_dependencias_despues.txt`
- `evidencia/C03/C03-03_persistencia.txt` y `C03-03_persistencia_reinicio.txt`
- `evidencia/C03/capturas/` (pruebas, venta en Swing, detalle de producto en sus tres estados, persistencia)

### Estado

**VERIFICADO:** 142 pruebas del backend; 34 pruebas de Angular; la venta se guarda en PostgreSQL con su partida y baja la existencia; tres casos de error no modifican la base; la venta persiste tras reiniciar la API.

**NO_VERIFICADO:** que no existan otras reglas de negocio duplicadas fuera de las rutas de venta; análisis de Sonar sobre TypeScript.

**PENDIENTE (cierre del corte, a cargo de Xochitl):** índice `evidencia/C03_EQUIPO_06.md`, análisis de Sonar final, reproducción de la guía en el equipo de Xochitl y etiqueta `v0.3-web`.

### Análisis de Sonar previo (C03)

Antes de hacer el commit corrí SonarQube sobre el backend con los cambios de #62, #63 y #64: Quality Gate Passed, 0 issues, cobertura de 91.7% sobre el código nuevo y 0.0% de duplicaciones (detalle y capturas en `evidencia/C03/C03_sonar_previo.md`). Es un análisis previo sobre la copia de trabajo; el análisis final se repite sobre `master` integrado. Sonar no analiza el código TypeScript del cliente.
