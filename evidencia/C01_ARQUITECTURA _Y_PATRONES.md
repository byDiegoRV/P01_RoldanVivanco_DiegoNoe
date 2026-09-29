# Reporte de Arquitectura y Diseño - Corte 1 (C01)

## 1. Modelo de Dominio
El núcleo del proyecto `tcsw-ventas` se basa en las entidades principales `Venta`, `DetalleVenta` y `Producto`.
* **Invariantes:** Una venta no puede registrarse si su lista de detalles está vacía. El cálculo del total debe procesar correctamente los subtotales de cada detalle y aplicar el descuento correspondiente antes del pago.
* **Estado:** VERIFICADO

## 2. Arquitectura Hexagonal
Se reestructuró el proyecto para garantizar el principio de inversión de dependencias:
* **Núcleo (Domain/Application):** Contiene las interfaces (puertos) y casos de uso (`RegistrarVentaUseCase`). Aislado de frameworks externos.
* **Adaptadores (Infrastructure):** Implementaciones de persistencia (ej. `VentaRepositoryInMemory`) ubicadas en `adapter/out/persistence`.
* **Estado:** VERIFICADO (Validado mediante `verify-module.sh M07`).

## 3. Patrones de Diseño Aplicados
### Patrón Strategy (Cálculo de Descuentos)
* **Problema:** Evitar múltiples sentencias condicionales al aplicar diferentes promociones.
* **Solución:** Interfaz `CalculadorDescuentoStrategy` con implementaciones concretas (`DescuentoVIP`, `DescuentoSinPromo`).
* **Evidencia visual:** ![Diagrama Strategy](./diagramas/PatronStrategy(CalculoDescuentos).png)

### Patrón Factory Method (Métodos de Pago)
* **Problema:** Desacoplar la instanciación de las formas de pago del flujo principal de ventas.
* **Solución:** Clase `PagoFactory` encargada de instanciar la interfaz `MetodoPago` (ej. `PagoEfectivo`).
* **Evidencia visual:** ![Diagrama Factory](./diagramas/PatronFactory(MetodosPago).png)