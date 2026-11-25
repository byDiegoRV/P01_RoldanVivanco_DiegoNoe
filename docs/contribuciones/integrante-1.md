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

**PENDIENTE:** etiqueta `v0.3-web` y entrega en Eminus, que se realizan al integrar el PR de cierre (#66).

### Análisis de Sonar previo (C03)

Antes de hacer el commit corrí SonarQube sobre el backend con los cambios de #62, #63 y #64: Quality Gate Passed, 0 issues, cobertura de 91.7% sobre el código nuevo y 0.0% de duplicaciones (detalle y capturas en `evidencia/C03/C03_sonar_previo.md`). Es un análisis previo sobre la copia de trabajo; el análisis final se repite sobre `master` integrado. Sonar no analiza el código TypeScript del cliente.

### Cierre del Corte 3 (issue #66)

Por falta de disponibilidad de tiempo de Xochitl, el cierre del corte lo realicé yo. Rama `feature/c03-cierre`.

| Trabajo | Archivos |
|---|---|
| Índice de evidencia ligado a los criterios de R04, con VERIFICADO, NO_VERIFICADO y PENDIENTE por separado | `evidencia/C03_EQUIPO_06.md` |
| Guía de reproducción del corte | `docs/guia-reproduccion-c03.md` |
| Sección del Corte 3 en el README | `README.md` |
| Análisis de SonarQube final del backend sobre `master` integrado (con reporte generado desde la API de SonarQube, sin credenciales) | `evidencia/C03/capturas/C03_sonar_final_resumen.png`, `evidencia/C03/capturas/C03_sonar_final_issues.png` |
| Verificación del índice antes del commit (rutas, marcadores y datos personales) | `scripts/c03-verificar.sh` |
| Etiqueta del corte | `v0.3-web` |

La evidencia de reproducción en un segundo equipo (issue #65) es de Xochitl (`docs/contribuciones/integrante-2.md`).

---

## C04 — Candidato de liberación (R05)

**Integrante:** Diego Noe Roldán Vivanco (`byDiegoRV`)
**Rama:** `feature/c04-contenedores` (PR #78) y `feature/c04-cierre`

| Issue | Trabajo realizado | Estado |
|---|---|---|
| #70 (C04-01) | Pila en contenedores: Dockerfile.api, Dockerfile.frontend, compose con healthchecks, `verify-stack.sh` | VERIFICADO |
| #71 (C04-02) | Recorrido de venta, módulo M14 en `verify-module.sh`, diagnóstico por fronteras (H-01, H-02, H-04) | VERIFICADO |
| #72 (C04-03) | Integración continua en GitHub Actions y corrección del bloqueo por diálogos modales (H-03) | VERIFICADO |
| #73 (C04-04) | SonarQube de backend y frontend, hallazgos corregidos | VERIFICADO (local) |
| #74 (C04-05) | Reporte de pruebas, matriz de riesgos y verificación de los dos modos de persistencia | VERIFICADO; R13 PENDIENTE |
| #75 (C04-06) | Plan de reversa a `v0.3-web` y ensayo real | VERIFICADO |
| #77 (C04-08) | README final, notas de versión, índice de evidencia, versiones coherentes, etiqueta y Release | ver `evidencia/C04_EQUIPO_06.md` |

**Pull requests:** #78 (issues #70 a #75) y el PR del cierre (#77).
**Pruebas:** 142 del backend y 34 del frontend (`evidencia/C04/C04-05_reporte_pruebas.txt`), `./scripts/verify-module.sh M14` (`C04-08_m14_final.txt`).
**Límites declarados:** R13 sin prueba; SonarQube solo local; ver `evidencia/C04_EQUIPO_06.md`, sección 4.

**Commits:**
- `2e6ca33` docs(evidencia): verificacion final M14 y versiones efectivas del candidato (refs #77)
- `58bddc5` docs(evidencia): indice C04 del equipo ligado a los criterios de R05 (refs #77)
- `408db8d` docs(version): notas de version v1.0-rc y hallazgos de la reproduccion (E-01 a E-03) (refs #77)
- `7242477` chore(version): alinear version del cliente con backend y contrato (1.0.0) (refs #77)
- `3ffb964` docs(readme): titulo del README actualizado al alcance P01 a C04 (refs #77)
- `4d4d394` docs(readme): seccion del candidato v1.0-rc, despliegue, problemas conocidos y Dockerfile historico (refs #77)
- `f96bcd7` docs(ci): historial final de ejecuciones antes del PR (refs #72)
- `5f29f70` docs(ci): historial final de ejecuciones antes del PR (refs #72)
- `480a32e` docs(reversa): completar resultados del ensayo en el plan (refs #75)
- `f062563` docs(evidencia): ensayo de reversa ejecutado, hallazgo H-05 y capturas (refs #75)
- `1d4b29b` feat(reversa): ensayo de reversa v1.0-rc a v0.3-web con respaldo y restauracion (refs #75)
- `e1a5d73` docs(evidencia): capturas de la verificacion de persistencia por modo (refs #74)
- `d57fb76` test(integracion): verificar recorrido con PERSISTENCIA=hibernate y jdbc (refs #74)
- `45c6c94` docs(evidencia): capturas de C04-05 (refs #74)
- `4d23bf8` docs(pruebas): matriz de riesgos, reporte de pruebas y cobertura del candidato (refs #74)
- `e6498e3` docs(sonar): analisis final, tratamiento de hallazgos y evidencia (refs #73)
- `679fbda` docs(evidencia): regenerar versiones efectivas con rutas ocultas (refs #70)
- `5a1b061` docs(evidencia): capturas de Sonar del backend y del frontend (C04-04) (refs #73)
- `edb14b8` feat(sonar): script del analisis del frontend en contenedor e ignorar .scannerwork (refs #73)
- `00cc02e` fix(contenedores): healthcheck del cliente con 127.0.0.1 (H-04) (refs #71)
- `d392b74` fix(frontend): caption accesible en tablas y eliminar app.css vacio (Sonar) (refs #73)
- `4507197` fix(test): reemplazar Thread.sleep por ScheduledExecutorService en CierraDialogosModales (Sonar S2925) (refs #73)
- `68741bb` docs(evidencia): ocultar rutas locales en C04-01_versiones (refs #70)
- `28408d7` docs(ci): registro de ejecuciones y evidencia (refs #72)
- `ad11bcb` ci: dar permiso de ejecucion a los scripts (refs #72)
- `66616ae` ci: cerrar dialogos modales en pruebas de Swing y limitar tiempos del flujo (refs #72)
- `cf6c9cf` ci: flujo de GitHub Actions (backend, frontend y pila completa) (refs #72)
- `ff85534` docs(evidencia): capturas de C04-02 (refs #71)
- `946585c` feat(integracion): verify-recorrido, soporte M14 y diagnostico por fronteras (refs #71)
- `b04b2d4` docs(evidencia): captura actualizada del caso de BD caida (refs #70)
- `e30b7ee` feat(contenedores): Dockerfile.api, Dockerfile.frontend, compose completo y verify-stack (refs #70)
