# Evidencia P10 — Equipo 06

**Actividad:** P10, M13 Angular básico (R01, 2 pts)
**Equipo:** Diego Noe Roldán Vivanco (`byDiegoRV`) y Xochitl Salgado (`xochitlsalgado`)
**Repositorio:** https://github.com/byDiegoRV/P01_RoldanVivanco_DiegoNoe
**Rama de entrega:** `master`
**Commit de entrega:** _tag o commit registrado al cerrar el PR de cierre (ver "Estado")_
**Verificación oficial:** `./scripts/verify-module.sh M13`

## 1. Alcance

Cliente Angular 20 que lista los productos y registra ventas consumiendo la API REST de P09 (puerto 9098). El pago es una simulación del cliente: no se envía a la API ni se guarda, y la API no se modificó. Instrucciones de arranque y pruebas en el `README.md` (sección "Cliente Angular (P10)") y en `docs/guia-reproduccion-p10.md`.

## 2. Rúbrica R01

| Criterio | Peso | Qué se entrega | Dónde está la evidencia | Estado |
|---|---|---|---|---|
| Funcionamiento | 0.8 | Catálogo con estados de carga, vacío y error; carrito, pago simulado y registro de venta con folio y total; errores de la API visibles | `evidencia/P10/P10-04_build.txt`; capturas `P10-04_venta_ok.png`, `P10-04_pago_simulado.png`, `P10-04_aviso_cantidad.png`, `P10-04_inventario_insuficiente.png` y `P10-04_sin_conexion.png` en `evidencia/P10/capturas/` | VERIFICADO |
| Solución técnica | 0.5 | Proxy de desarrollo en lugar de CORS, modelos tipados según `docs/openapi.yaml`, servicios HTTP con conversión de errores al formato de la API, componentes standalone con señales | `frontend/proxy.conf.json`, `frontend/src/app/modelos/`, `frontend/src/app/servicios/`, `frontend/src/app/catalogo/`, `frontend/src/app/venta/`; sección de decisiones en `docs/contribuciones/integrante-1.md` | VERIFICADO |
| Pruebas | 0.3 | 27 pruebas de servicio (con `HttpTestingController`) y de componentes, con casos positivos y de error | `evidencia/P10/P10-04_test.txt`, `evidencia/P10/P10-05_lista-pruebas.txt`, `evidencia/P10/P10-05_cobertura.txt`, `evidencia/P10/P10-06_verify-m13-xochitl.txt` | VERIFICADO |
| Calidad y Sonar | 0.2 | Análisis de SonarQube del backend (Java); cobertura medida del cliente | <<COMPLETADO: 84% 0 Issues  ; `evidencia/P10/P10-05_cobertura.txt` | VERIFICADO (backend); NO_VERIFICADO (TypeScript) |
| Git y evidencia | 0.2 | Ramas por integrante, commits con `refs #N`, PR #59 (Noe) y #60 (Xochitl), índice de evidencia y contribuciones | Historial de `master`; `docs/contribuciones/integrante-1.md` y `integrante-2.md`; este archivo | VERIFICADO |

## 3. Casos positivos y de error de las pruebas

Los nombres exactos de cada prueba están en `evidencia/P10/P10-05_lista-pruebas.txt`.

| Archivo de pruebas | Pruebas | Casos positivos | Casos de error |
|---|---|---|---|
| `servicios/productos.service.spec.ts` | 3 | listar productos con `GET /api/productos` | falta de conexión; 404 convertido al formato de error de la API |
| `servicios/ventas.service.spec.ts` | 4 | registrar una venta con `POST /api/ventas` y recibir folio y total | 400 de validación con detalle por campo; 409 de existencia insuficiente; respuesta que no es del formato de la API sin exponer detalles internos |
| `servicios/errores-api.spec.ts` | 5 | conserva el error con el formato de la API; detalles vacíos cuando no vienen | falta de conexión (status 0); respuesta desconocida; no expone detalles internos de un error inesperado |
| `catalogo/catalogo.component.spec.ts` | 5 | lista cuando la API responde; estado de carga | falta de conexión; mensaje cuando no hay productos; mensaje de la API al fallar, con opción de reintentar |
| `venta/venta.component.spec.ts` | 8 | productos en el selector; agrega al carrito y calcula el total; registra la venta sin enviar el pago y borra los datos de pago | cantidad mayor a la existencia; carrito vacío; tarjeta sin 16 dígitos; nombre del titular vacío; error de la API conservando el carrito |
| `app.spec.ts` | 2 | crea la aplicación; incluye el contenedor de rutas | — |
| **Total** | **27** | | |

## 4. Reproducción

- Guía: `docs/guia-reproduccion-p10.md` (requisitos con versiones, arranque, recorrido, casos de error y problemas frecuentes).
- Bitácora de errores: `docs/registro-errores-p10.md`.
- Versiones del equipo de Xochitl: `evidencia/P10/P10-06_versiones-xochitl.txt`.
- La guía se siguió en dos equipos distintos (Windows 11, Git Bash), con Node 24.11.0 / npm 11.6.2 y con Node 24.21.0 / npm 11.19.0.

## 5. Índice de archivos de evidencia

| Archivo | Contenido |
|---|---|
| `evidencia/P10/P10-04_build.txt` | Versiones y salida de `ng build` (equipo de Noe) |
| `evidencia/P10/P10-04_test.txt` | Resumen de `ng test` (equipo de Noe) |
| `evidencia/P10/P10-05_cobertura.txt` | Cobertura del cliente medida con `ng test --code-coverage` |
| `evidencia/P10/P10-05_lista-pruebas.txt` | Archivos y nombres de las pruebas |
| `evidencia/P10/P10-06_verify-m13-xochitl.txt` | `verify-module.sh M13` en el equipo de Xochitl: `MODULO_M13_VERIFICADO` |
| `evidencia/P10/P10-06_versiones-xochitl.txt` | Versiones de herramientas en el equipo de Xochitl |
| `evidencia/P10/P10-07_verify-m13.txt` | `verify-module.sh M13` en el equipo de Noe: `MODULO_M13_VERIFICADO` |
| `evidencia/P10/capturas/` | Capturas del recorrido funcional (`P10-04_*.png`) |
| `evidencia/EVIDENCIA_XOCHITL/P10.md` | Evidencia personal de Xochitl |
| `scripts/verify-module.sh` | Verificación oficial, con soporte para M13 |
| `scripts/evidencia-p10.sh` | Genera las salidas de build y pruebas sin rutas personales |

## 6. Estado

**VERIFICADO**
- Compilación (`ng build`) y 27 pruebas en SUCCESS, en los equipos de ambas integrantes.
- `./scripts/verify-module.sh M13` con `MODULO_M13_VERIFICADO` en ambos equipos.
- Recorrido manual contra la API en el puerto 9098: catálogo, venta con folio, validación de cantidad, existencia insuficiente, pago simulado y error sin conexión (capturas `P10-04_*.png`).
- Cobertura del cliente medida (`P10-05_cobertura.txt`).
- Análisis de SonarQube del backend.

**NO_VERIFICADO**
- Análisis de Sonar sobre el código TypeScript del cliente (el análisis cubre el backend).
- Pruebas de extremo a extremo automatizadas con navegador (no requeridas por la actividad).
- Reproducción en un clon nuevo desde cero: la guía se siguió en clones existentes, con instalación limpia de dependencias (`npm ci`).
- Venta con folio y error sin conexión en el equipo de Xochitl: no hay captura propia; esos casos están verificados en el equipo de Noe.

**PENDIENTE**
- Tag o commit de entrega y entrega en Eminus.

## 7. Notas de alcance

- El pago es una simulación del cliente; la API de P09 no procesa pagos y no se modificó.
- No hay credenciales, contraseñas ni rutas personales en el repositorio ni en las salidas de esta evidencia; el archivo `.env` no se versiona.
