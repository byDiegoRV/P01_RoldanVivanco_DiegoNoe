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
<URL_DEL_REPOSITORIO> 
```

*(Completar: pega aqui la URL de tu repositorio en GitHub, la misma que
reportaste en P01 si es el mismo repositorio acumulativo, seguida de
`/tree/master` o la rama correspondiente.)*

## Identificador del commit final

```
<COMMIT_FINAL_P02>
```

*(Completar: pega aqui la salida real de `git log -1 --format="%H"` DESPUES
de hacer `git add`, `git commit` y `git push` de los archivos de P02.)*

## Ambiente en el que se ejecuto la verificacion

- Sistema: Windows 11 10.0 (amd64).
- JDK: 25.0.2 (Oracle Corporation, 64-bit). El `pom.xml` fija
  `<release>11</release>` como objetivo de compilacion; un JDK mas nuevo
  compila igual hacia esa release (ver discrepancia registrada en
  `README.md`, seccion 1).
- Maven: *(completar con la salida real de `mvn -version` en tu maquina)*.
- SonarQube Server: 9.9.8.100196, corriendo localmente en Docker
  (`sonarqube:lts-community`).

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
  -Dsonar.login=<TOKEN_DE_USUARIO_PROPIO>
```

Resultado real observado: `ANALYSIS SUCCESSFUL`. Primera ejecucion: 5
hallazgos, todos Code Smell (0 Bugs, 0 Vulnerabilities) — ver el detalle
completo, regla por regla, en `README.md` seccion 8. Los 4 hallazgos
`Major` (`java:S5778`, lambdas de `assertThrows` con mas de una invocacion)
se corrigieron en el codigo. El hallazgo `Info` (`java:S1135`) se identifico
como falso positivo (la regla detecto la subcadena "TODO" dentro de la
palabra en espanol "todos") y se marco como *False Positive* en Sonar con
comentario justificativo.

Segunda ejecucion (tras las correcciones): **0 issues abiertos**,
**Quality Gate: Passed**.

## Declaracion de resultados (VERIFICADO / NO_VERIFICADO / PENDIENTE)

- **VERIFICADO** — Compilacion del codigo fuente (`main` y `test`) con el
  JDK instalado, hacia `release 11`.
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
  hallazgo restante justificado como falso positivo (ver seccion anterior).
- **PENDIENTE** — Push del commit final al repositorio remoto y
  verificacion de accesibilidad publica de la URL (ver seccion "URL del
  repositorio" arriba, a completar tras el `git push`).

## Manejo responsable de datos

No se incluyen credenciales, datos personales ni rutas privadas en este
repositorio. El token de SonarQube usado para el analisis es personal, no
se comparte ni se sube al repositorio ni a este documento.