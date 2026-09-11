# P04 — Archivo breve de evidencia

**Experiencia educativa:** Tecnologías para la Construcción de Software

**Actividad:** P04 — Integración y verificación de módulo

**Módulo:** M04. Calidad, pruebas y verificación de integración

## URL del repositorio

```
https://github.com/byDiegoRV/P01_RoldanVivanco_DiegoNoe/tree/master

```

Mismo repo de prácticas anteriores (`tcsw-ventas`).

## Commit final (en master, tras fusionar y etiquetar)

```
<COMPLETAR: git log -1 --format="%H" en master, despues de la fusion>

```

Tag de esta entrega: `v0.4.0`

## Ambiente

* Windows 11, JDK 11.0.32, Apache Maven 3.9.13.
* SonarQube 9.9.8.100196 en Docker local.

## Qué se hizo

Se integraron las aportaciones individuales de los integrantes del equipo hacia la rama principal (`master`), resolviendo los conflictos de código presentados en archivos compartidos como `Venta.java` y `VentaTest.java`.

Por la parte individual (Diego Noé):

* Se implementó el método `estaAgotado()` en la clase `Producto.java` para verificar si las existencias son menores o iguales a cero.
* Se agregaron las pruebas unitarias correspondientes en `ProductoTest.java` para validar los escenarios de stock disponible y agotado.
* Se participó en el control de calidad cruzado mediante la revisión del Pull Request #3 (formateo del total de la venta).

## Pruebas

```
mvn clean test
Tests run: 44, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS

```

## Verificación del Módulo

Se actualizó el script de validación local y se ejecutó la comprobación automatizada:

```
bash scripts/verify-module.sh M04

```

Resultado obtenido en consola: `MODULO_M04_VERIFICADO`.

## SonarQube

Análisis estático ejecutado con SonarQube: Quality Gate en **Passed**, con 0 bugs, 0 vulnerabilidades y 0 *code smells* sobre las métricas del nuevo código.

## Declaracion de resultados

* **VERIFICADO** — Integración exitosa de las ramas individuales de todo el equipo en `master`.
* **VERIFICADO** — 44 pruebas unitarias pasando sin errores (incluyendo las nuevas de existencia y formateo).
* **VERIFICADO** — Script de ejecución por módulo completado correctamente (`MODULO_M04_VERIFICADO`).
* **VERIFICADO** — SonarQube con Quality Gate en **Passed** y cobertura auditada con JaCoCo.
* **VERIFICADO** — Repositorio remoto actualizado con los cambios y tag `v0.4.0`.