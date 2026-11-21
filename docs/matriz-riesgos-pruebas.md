# Matriz de riesgos y pruebas — candidato v1.0-rc (C04)

Fuente de las cifras: `evidencia/C04/C04-05_reporte_pruebas.txt` (generado por `scripts/evidencia-c04-pruebas.sh`) y la ejecución de CI (`docs/integracion-continua.md`).
Estados: **VERIFICADO** (hay prueba ejecutada y pasando) · **PENDIENTE** (sin prueba; con causa y plan) · **LÍMITE** (fuera de alcance, declarado).

## 1. Resultados de pruebas
| Capa | Pruebas | Resultado | Cobertura de líneas | Cobertura de ramas |
|---|---|---|---|---|
| Backend (JUnit 5, JaCoCo, PostgreSQL 16) | 142 | 0 fallos, 0 errores | 87,7 % (907/1034) | 71,4 % (167/234) |
| Frontend (Karma/Jasmine, ChromeHeadless) | 34 | 34 SUCCESS | 95,07 % (135/142) · sentencias 93,58 % | 84,21 % (32/38) · funciones 85,71 % |
| Pila completa (`verify-stack` + `verify-recorrido`) | script de integración | `STACK_C04_VERIFICADO` / `RECORRIDO_VERIFICADO` | no medida por JaCoCo | — |

Cobertura del backend por paquete (JaCoCo, líneas / ramas):

| Paquete | Líneas | Ramas |
|---|---|---|
| application.service | 96,5 % | 86,4 % |
| domain | 91,1 % | 81,0 % |
| infrastructure.delivery.swing.controlador | 94,3 % | 88,1 % |
| infrastructure.delivery.swing.vista | 95,8 % | 62,5 % |
| infrastructure.persistence.jdbc | 89,0 % | 67,9 % |
| infrastructure.persistence.hibernate | 88,2 % | 50,0 % |
| infrastructure.delivery.rest | 64,8 % | 54,2 % |
| adapter.in.swing (vista heredada de P-previas) | 42,5 % | 0,0 % |
| (resto: DTO, excepciones, puertos, modelos: 100 %) | | |

## 2. Matriz
| # | Riesgo | Frontera | Prueba(s) que lo cubre(n) | Estado | Observación |
|---|---|---|---|---|---|
| R1 | Vender más de lo que hay (existencia insuficiente) | Aplicación / Persistencia | `VenderProductosServiceTest`, `VentasRestControllerTest`, `HibernateVentaAdapterTest`, `VentaJdbcRepositoryTest`; recorrido [B] (409 `EXISTENCIA_INSUFICIENTE`) | VERIFICADO | |
| R2 | Venta parcial: se guarda el encabezado pero no las partidas o no se descuenta existencia | Persistencia | adaptadores `Hibernate*` y `Jdbc*` (`*VentaAdapterTest`, `VentaJdbcRepositoryTest`); recorrido [A] y atomicidad en [B] | VERIFICADO | |
| R3 | La API se aparta del contrato OpenAPI | HTTP / Aplicación | `ManejadorErroresTest`, `VentasRestControllerTest`, `ProductosRestControllerTest`, `ProductosListaVaciaTest`; recorrido [B] (9 casos) | VERIFICADO | H-02 corrigió el servidor declarado en el contrato |
| R4 | Los errores exponen trazas, SQL, credenciales o datos de tarjeta | HTTP | `ManejadorErroresTest`; recorrido [B] (cuerpos sin `Exception`/`SQLException`/`password`; campo `tarjeta` rechazado con 400) | VERIFICADO | |
| R5 | Entrada inválida (cantidad 0, id no numérico, JSON mal formado) | HTTP / Aplicación | `*RestControllerTest`, `ManejadorErroresTest`; recorrido [B] | VERIFICADO | |
| R6 | La API responde bien pero la BD no refleja el efecto | Persistencia | recorrido [A] (fila en `ventas`, partida en `detalle_venta`, existencia descontada y catálogo actualizado); evidencia C03 de persistencia y reinicio | VERIFICADO | |
| R7 | El cliente confunde fallo de red (estado 0) con fallo del backend (500) | Formulario / HTTP | `errores-api.spec.ts`, `ventas.service.spec.ts`, `productos.service.spec.ts`; `verify-stack.sh --con-caida` (BD caída → error controlado y recuperación) | VERIFICADO | El estado 0 se prueba en el cliente con servicios simulados |
| R8 | El cliente no muestra estados de carga, vacío o error | Formulario | `catalogo.component.spec.ts`, `producto-detalle.component.spec.ts`, `venta.component.spec.ts` | VERIFICADO | |
| R9 | Reglas de negocio mal aplicadas (descuentos, formas de pago) | Aplicación | `CalculadorDescuentoTest`, `DescuentoVIPTest`, `DescuentoSinPromoTest`, `PagoEfectivoTest`, `PagoFactoryTest`, `ProductoTest`, `VentaTest`, `DetalleVentaTest` | VERIFICADO | |
| R10 | El despliegue no se reproduce fuera de la máquina del autor | Contenedores / CI | CI (jobs `backend`, `frontend`, `pila`), `verify-stack.sh`, versiones congeladas; reproducción por Xochitl (C04-07) | VERIFICADO (CI) · PENDIENTE (C04-07) | |
| R11 | Un cambio de versión rompe el candidato | Todas | versiones congeladas (`docs/versiones-congeladas.md`), `npm ci`, CI | VERIFICADO | Las advertencias de obsolescencia de las acciones de CI se dejan a propósito |
| R12 | Una reversa pierde datos o no restaura el sistema | Despliegue / Persistencia | plan de reversa y su prueba (issue #75) | PENDIENTE | Se cubre en C04-06 |
| R13 | Dos ventas simultáneas sobre la misma existencia (concurrencia) | Persistencia | no hay prueba | PENDIENTE | Causa: no se diseñó una prueba de concurrencia. Plan: prueba con dos peticiones paralelas y comprobar que la existencia no queda negativa |
| R14 | Cobro real con tarjeta | Aplicación | — | LÍMITE | El pago es simulado en el cliente; la API no recibe datos de tarjeta (R4 lo comprueba) |
| R15 | Rendimiento y carga | Todas | no medido | LÍMITE | Fuera de alcance del curso en este corte |
| R16 | La variante `PERSISTENCIA=jdbc` no funciona en la pila real | Aplicación / Persistencia | adaptadores JDBC con pruebas propias (`PostgresProductoAdapterTest`, `VentaJdbcRepositoryTest`); no hay recorrido completo con JDBC | PENDIENTE | La pila en contenedores usa Hibernate por defecto; no se verificó el recorrido con `PERSISTENCIA=jdbc` |

## 3. Interpretación de la cobertura
- **Núcleo de negocio y persistencia bien cubiertos:** servicios de aplicación (96,5 %), dominio (91,1 %), controlador Swing (94,3 %) y adaptadores JDBC e Hibernate (88–89 %).
- **Ramas más débiles:** Hibernate (50 %) y vistas Swing (62,5 %): hay decisiones (caminos de error de configuración o de la interfaz) que ninguna prueba ejercita. No se persiguió el 100 % por no aportar riesgo relevante; se declara.
- **Capa REST (64,8 % de líneas):** el recorrido contra la pila real (`verify-recorrido.sh`) ejercita rutas y manejo de errores que JaCoCo no mide cuando se ejecutan en el contenedor. Las clases concretas con menos de 70 % están listadas en el apartado [3] de `C04-05_reporte_pruebas.txt`: (completar con 1–2 frases tras ver ese listado).
- **`adapter.in.swing` (42,5 %):** vista heredada de entregas anteriores (`VentaFrame`), con dos clases de prueba mínimas. Riesgo bajo: no participa en el recorrido web. Declarado.
- La cobertura del frontend (95 % de líneas) no se integra a SonarQube (ver `docs/tratamiento-sonar-c04.md`).
- Las clases por debajo del 70 % son ApiApplication (33,3 %), ConfiguracionApi y su clase interna Persistencia (0 %): el arranque y la configuración de Spring, que solo se ejecutan con la aplicación real y no en las pruebas unitarias. Los controladores y el manejo de errores sí están cubiertos; el arranque se ejerce con la pila real (verify-stack, verify-recorrido), que JaCoCo no mide.
