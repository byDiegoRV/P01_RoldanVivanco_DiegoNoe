# Evidencia C03 — Equipo 06

**Actividad:** C03, Corte 3: Integración web (R04, 5 pts)
**Equipo:** Diego Noe Roldán Vivanco (`byDiegoRV`) y Xochitl Salgado (`xochitlsalgado`)
**Repositorio:** https://github.com/byDiegoRV/P01_RoldanVivanco_DiegoNoe
**Rama de entrega:** `master`
**Etiqueta del corte:** `v0.3-web`
**Verificación oficial:** `./scripts/verify-module.sh M13`
**Guía de reproducción:** `docs/guia-reproduccion-c03.md`

## 1. Alcance

Integración de las tres entradas del sistema sobre el mismo núcleo: API REST (puerto 9098), cliente Angular (consume la API mediante proxy) e interfaz Swing de escritorio. El corte agrega la reutilización del caso de uso de venta en Swing, la pantalla de detalle de producto en Angular y la evidencia de persistencia de extremo a extremo con casos de error. El pago es una simulación de la interfaz: no se envía a la API ni se guarda.

## 2. Evidencia mínima del corte

| Evidencia pedida | Archivo | Estado |
|---|---|---|
| Etiqueta | `v0.3-web` (ver sección 6) | PENDIENTE al integrar este archivo |
| Pruebas API | `evidencia/C03/C03-04_backend_xochitl.txt`, `evidencia/C03/capturas/01_pruebas_api.png` | VERIFICADO |
| Prueba Angular | `evidencia/C03/C03-04_angular_xochitl.txt`, `evidencia/C03/C03-04_verify-m13_xochitl.txt` | VERIFICADO |
| Persistencia | `evidencia/C03/C03-03_persistencia.txt`, `evidencia/C03/C03-03_persistencia_reinicio.txt`, `evidencia/C03/C03-04_persistencia_xochitl.txt`, `evidencia/C03/C03-04_persistencia_reinicio_xochitl.txt` | VERIFICADO |

## 3. Rúbrica R04

| Criterio | Peso | Qué se entrega | Dónde está la evidencia | Estado |
|---|---|---|---|---|
| API | 1.5 | Contrato `docs/openapi.yaml`; endpoints de productos y ventas funcionando; ventas guardadas en PostgreSQL | `docs/openapi.yaml`; `evidencia/C03/C03-04_curl-p09_xochitl.txt`; `evidencia/C03/C03-03_persistencia.txt`; `evidencia/C03/C03-04_backend_xochitl.txt` | VERIFICADO |
| Angular | 1.5 | Consumo de la API, navegación catálogo, detalle y venta, estados de carga, éxito, vacío y error | `frontend/src/app/producto/`; `frontend/src/app/catalogo/`; `frontend/src/app/venta/`; `frontend/src/app/app.routes.ts`; `evidencia/C03/C03-04_angular_xochitl.txt`; `evidencia/C03/capturas/C03-04_exito_detalle_xochitl.png`; `evidencia/C03/capturas/C03-02_venta_ok.png`; `evidencia/C03/capturas/C03-04_vacio_xochitl.png` | VERIFICADO |
| Reutilización del núcleo | 1 | Swing y la API ejecutan el mismo caso de uso (`VenderProductosUseCase`); se eliminó la orquestación duplicada | `docs/reutilizacion-nucleo.md`; `evidencia/C03/C03-01_dependencias_antes.txt`; `evidencia/C03/C03-01_dependencias_despues.txt`; `evidencia/C03/capturas/01_swing_venta.png`; `evidencia/C03/capturas/01_api_existencia.png` | VERIFICADO |
| Errores y validación | 0.5 | 409 por existencia insuficiente, 404 por producto inexistente, 400 por venta vacía; validación del nombre y de la tarjeta en las dos interfaces; mensajes de error visibles | `evidencia/C03/C03-04_persistencia_xochitl.txt`; `evidencia/C03/capturas/C03-02_detalle_404.png`; `evidencia/C03/capturas/C03-04_error_nombre_xochitl.png`; `evidencia/C03/capturas/C03-02_detalle_sin_conexion.png`; `docs/registro-errores-c03.md` | VERIFICADO |
| Calidad | 0.5 | 142 pruebas de backend y 34 de Angular; análisis de SonarQube del backend; issues, PR y contribuciones ligados | `evidencia/C03/C03-04_backend_xochitl.txt`; `evidencia/C03/C03-04_angular_xochitl.txt`; `evidencia/C03/C03_sonar_previo.md`; `evidencia/C03/capturas/C03_sonar_final_resumen.png`; `evidencia/C03/capturas/C03_sonar_final_issues.png`; `docs/contribuciones/integrante-1.md`; `docs/contribuciones/integrante-2.md` | VERIFICADO (backend); NO_VERIFICADO (TypeScript) |

## 4. Reproducción

- Guía: `docs/guia-reproduccion-c03.md`.
- Bitácora de errores: `docs/registro-errores-c03.md`.
- Versiones del segundo equipo: `evidencia/C03/C03-04_versiones_xochitl.txt`.
- Los scripts `scripts/evidencia-c03-dependencias.sh`, `scripts/evidencia-c03-persistencia.sh` y `scripts/evidencia-c03-pruebas.sh` generan las salidas sin credenciales ni rutas personales.

## 5. Issues y responsables

| Issue | Trabajo | Responsable |
|---|---|---|
| #62 | Reutilización del núcleo | Noe |
| #63 | Navegación Angular: detalle de producto y validación del nombre | Noe |
| #64 | Persistencia de extremo a extremo | Noe |
| #65 | Pruebas y casos de éxito, vacío y error en un segundo equipo | Xochitl |
| #66 | Cierre del corte | Noe (asumido por falta de disponibilidad de Xochitl) |

## 6. Estado

**VERIFICADO**
- Backend: 142 pruebas sin fallos, en el equipo de cada integrante (Java 11 y Java 23).
- Angular: 34 pruebas en SUCCESS, y `MODULO_M13_VERIFICADO` en el equipo de Xochitl.
- Persistencia: la venta se guarda con su partida, baja la existencia y sobrevive al reinicio de la API; los tres rechazos (409, 4xx y 400) no modifican la base.
- Casos de éxito, vacío y error del cliente (capturas).
- Análisis de SonarQube del backend (previo y final).

**NO_VERIFICADO**
- Análisis de Sonar sobre el código TypeScript del cliente.
- Pruebas de extremo a extremo automatizadas con navegador.
- Que no existan otras reglas de negocio duplicadas fuera de las rutas de venta.
- Reproducción en un clon nuevo desde cero (se usaron clones existentes con instalación limpia de dependencias).
- Venta con folio, producto inexistente (404) y error sin conexión: las capturas son del equipo de Noe; en el equipo de Xochitl no hay captura propia de esos tres casos (sí están sus resultados de pruebas y de persistencia).

**PENDIENTE**
- Etiqueta `v0.3-web` y entrega en Eminus, que se realizan al integrar este archivo.

## 7. Notas de alcance

- Las clases de descuento y de pago (`DescuentoVIP`, `DescuentoSinPromo`, `CalculadorDescuentoStrategy`, `MetodoPago`, `PagoEfectivo`, `PagoFactory`) y el código anterior (`VentaFrame`, `VentaRepositoryInMemory`) no forman parte del flujo de este corte.
- El pago es una simulación de la interfaz; la API no procesa pagos.
- No hay credenciales ni rutas personales en el repositorio; el `.env` no se versiona.
