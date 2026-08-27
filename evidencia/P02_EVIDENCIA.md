# P02 — Archivo breve de evidencia

**Experiencia educativa:** Tecnologias para la Construccion de Software
**Actividad:** P02 — Venta en memoria
**Modulo:** M02. Modelado orientado a objetos

## Indice de evidencia (relacion con criterios de R01)

| Evidencia | Archivo | Criterio de R01 que respalda |
|---|---|---|
| Diagrama de dominio | `README.md`, seccion 6 (diagrama Mermaid) | Saber teorico (composicion, identidad, objeto de valor) |
| Codigo de dominio — linea de venta | `src/main/java/mx/uv/tcsw/ventas/DetalleVenta.java` | Saber teorico (objeto de valor, invariantes) |
| Codigo de dominio — venta | `src/main/java/mx/uv/tcsw/ventas/Venta.java` | Saber teorico (identidad, composicion, invariantes) |
| Pruebas de `DetalleVenta` | `src/test/java/mx/uv/tcsw/ventas/DetalleVentaTest.java` | Saber heuristico (creacion valida y rechazo de datos invalidos) |
| Pruebas de `Venta` | `src/test/java/mx/uv/tcsw/ventas/VentaTest.java` | Saber heuristico (operaciones de venta completa, preservacion de estado) |
| Script de verificacion (M01/M02) | `scripts/verify-module.sh` | Saber heuristico (ejecucion declarada y reproducible) |
| Guia de instalacion y uso actualizada | `README.md` | Saber heuristico (reconstruccion por terceros) |
| Analisis estatico | `README.md`, seccion 8 (SonarQube) | Saber heuristico y axiologico (calidad, correccion o justificacion de hallazgos) |
| Este documento | `evidencia/P02_EVIDENCIA.md` | Saber axiologico (comunicacion profesional, trazabilidad) |

## URL del repositorio

```
https://github.com/byDiegoRV/P01_RoldanVivanco_DiegoNoe/tree/master
```

Es el mismo repositorio que use en P01, ya que el proyecto es acumulativo.

## Identificador del commit final

```
dfda392b682d619c5b53e6b9c787034670b0e75f
```

Lo obtuve con `git log -1 --format="%H"` despues de subir todo lo de P02
(codigo, pruebas, README actualizado y este archivo de evidencia).

## Ambiente en el que se ejecuto la verificacion

- Sistema: Windows 11 (amd64).
- JDK: 11.0.32 (Oracle Corporation, 64-bit), version LTS.
- Maven: Apache Maven 3.9.13.
- SonarQube Server: 9.9.8.100196, corriendo localmente en Docker
  (`sonarqube:lts-community`).

Nota: cuando arranque P02 todavia tenia instalado el JDK 25 (el `pom.xml`
compilaba igual hacia `release 11` sin ningun problema), pero despues
cambie a instalar el JDK 11 directamente en mi maquina, asi que el
ambiente reportado aqui ya es el definitivo.

## Comando ejecutado y resultado real

Comando declarado en la guia: `mvn clean test` (o equivalentemente,
`./scripts/verify-module.sh M02`), ejecutado desde la raiz de
`tcsw-ventas/`.

Resultado real observado:

```
Tests run: 39, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Desglose: 17 pruebas de `ProductoTest` (P01) + 9 pruebas de
`DetalleVentaTest` (P02) + 13 pruebas de `VentaTest` (P02).

## Analisis estatico (SonarQube)

Comando ejecutado:

```
mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.projectKey=tcsw-ventas \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<sqa_bf12a565ff78538d340bc701b3728b811ecfaac4>
```



Resultado real observado: `ANALYSIS SUCCESSFUL`. En la primera ejecucion
salieron 5 hallazgos, todos Code Smell (0 Bugs, 0 Vulnerabilities) — el
detalle completo, regla por regla, esta en `README.md` seccion 8. Los 4
hallazgos `Major` (`java:S5778`, lambdas de `assertThrows` con mas de una
invocacion) los corregi en el codigo. El hallazgo `Info` (`java:S1135`)
resulto ser un falso positivo (la regla detecto la subcadena "TODO" dentro
de la palabra "todos"), asi que lo marque como *False Positive* en Sonar
con un comentario explicando por que.

Segunda ejecucion, ya con las correcciones aplicadas: **0 issues
abiertos**, **Quality Gate: Passed**.

## Declaracion de resultados (VERIFICADO / NO_VERIFICADO / PENDIENTE)

- **VERIFICADO** — Compilacion del codigo fuente (`main` y `test`) con
  Java 11.
- **VERIFICADO** — Ejecucion de las 39 pruebas JUnit 5 (`ProductoTest`,
  `DetalleVentaTest`, `VentaTest`), todas en verde, incluyendo casos
  positivos, negativos/limite y de preservacion de estado tras un rechazo.
- **VERIFICADO** — Modelo de dominio de P02: `DetalleVenta` como objeto de
  valor con precio congelado, `Venta` como entidad con identidad propia
  (`folio`) que compone `DetalleVenta`, reutilizando
  `Producto.descontar(cantidad)` de P01 para validar cantidad y existencia
  antes de registrar una partida.
- **VERIFICADO** — Analisis estatico con SonarQube: 0 issues abiertos,
  Quality Gate en Passed, con los 4 hallazgos reales corregidos y el
  hallazgo restante justificado como falso positivo.
- **VERIFICADO** — Push del commit final al repositorio remoto; la URL es
  la misma de P01 y ya confirme que es accesible.

