# Reutilización del núcleo — C03 (R04)

El sistema tiene tres entradas (API REST, cliente Angular a través de la API, y la interfaz Swing de escritorio). Todas deben ejecutar las mismas reglas de negocio, definidas una sola vez en el núcleo (`domain` y `application`) y no copiadas en cada interfaz.

## 1. Qué se comparte

| Elemento | Dónde vive | Quién lo usa |
|---|---|---|
| Reglas de la venta (partidas, existencia, total) | `domain`: `Venta`, `Producto`, `DetalleVenta`, `ExistenciaInsuficienteException` | API REST y Swing, a través del caso de uso |
| Caso de uso "vender productos" (folio, lectura fresca de existencias, armado de la venta, registro) | `application`: `VenderProductosUseCase` / `VenderProductosService` | `VentasRestController` (API) y `VentasController` (Swing) |
| Registro y validación de la venta (no vacía, persistencia) | `application`: `RegistrarVentaService` | `VenderProductosService` |
| Persistencia | puertos `ProductoRepository` y `VentaRepository`; adaptadores JDBC e Hibernate intercambiables | el núcleo; ninguna interfaz conoce SQL |
| Contrato HTTP | `docs/openapi.yaml` y DTO de `infrastructure/delivery/rest` | API y cliente Angular (modelos tipados que lo reflejan) |

El cliente Angular no duplica el núcleo: consume la API y sus verificaciones de total y existencia son una ayuda de la interfaz; la regla autoritativa es la del dominio, que la API devuelve como 409.

## 2. Duplicación detectada y corregida (issue C03-01)

Antes: el controlador Swing (`VentasController.construirVenta()`) repetía la orquestación que ya hacía `VenderProductosService` en la API: generar el folio con el mismo formato, leer cada producto de nuevo, agregar la partida y registrar. Eran dos copias del mismo caso de uso, una en una interfaz y otra en la capa de aplicación.

Ahora: el controlador Swing solo convierte el carrito en `ItemVenta` (id y cantidad) y llama a `VenderProductosUseCase`, el mismo puerto que usa la API. `construirVenta()` y el formato de folio duplicado se eliminaron del controlador. `MainSwing` arma `VenderProductosService(productos, new RegistrarVentaService(ventas))`.

Evidencia: `evidencia/C03/C03-01_dependencias_antes.txt` frente a `evidencia/C03/C03-01_dependencias_despues.txt`, generadas con `scripts/evidencia-c03-dependencias.sh`. En la sección "Dónde se arma una venta" el código de arma-venta aparece antes en el dominio, la API y Swing; después solo en el dominio y en el caso de uso.

## 3. Lo que sigue siendo específico de cada interfaz (y por qué)

| Regla | Dónde | Justificación |
|---|---|---|
| Validación de nombre y tarjeta de 16 dígitos | Swing (regex) y Angular (su propia validación) | El pago es una simulación de la interfaz: la API y el dominio no procesan pagos ni lo reciben. No es una regla de negocio del núcleo |
| Mensajes y estados de pantalla | Swing (`JOptionPane`) y Angular (señales y estados de carga, vacío y error) | Presentación propia de cada tecnología |
| Conversión de excepciones a HTTP | `ManejadorErrores` (REST) | Es traducción de protocolo; la regla que dispara el error está en el dominio |

## 4. Declaraciones

- Las clases `DescuentoVIP`, `DescuentoSinPromo`, `CalculadorDescuentoStrategy`, `MetodoPago`, `PagoEfectivo` y `PagoFactory` no se usan en el flujo web ni en el de Swing; no forman parte de lo que se entrega en este corte.
- `adapter/in/swing/VentaFrame` y `adapter/out/persistence/VentaRepositoryInMemory` son código anterior que solo usan algunas pruebas; no forman parte del flujo web.
- NO_VERIFICADO: que no exista ninguna otra copia de reglas de negocio fuera de las listadas; la revisión se hizo sobre las rutas de venta, no sobre todo el código.
