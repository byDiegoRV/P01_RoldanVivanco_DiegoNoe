# P01 — Archivo breve de evidencia

**Experiencia educativa:** Tecnologias para la Construccion de Software (TCSW-19234)
**Actividad:** P01 — Ambiente reproducible y Producto
**Modulo:** M01. Ambiente y bases de POO

## Indice de evidencia (relacion con criterios de R01)

| Evidencia | Archivo | Criterio de R01 que respalda |
|---|---|---|
| Codigo fuente de la entidad | `src/main/java/mx/uv/tcsw/ventas/Producto.java` | Saber teorico (encapsulamiento e invariantes) |
| Pruebas automatizadas | `src/test/java/mx/uv/tcsw/ventas/ProductoTest.java` | Saber heuristico (creacion valida y rechazo de datos invalidos) |
| Proyecto Maven | `pom.xml` | Saber heuristico (ambiente reproducible, ciclo Maven) |
| Guia de instalacion y uso | `README.md` | Saber heuristico (reconstruccion por terceros) |
| Ambiente por contenedor | `Dockerfile` | Saber heuristico (reproducibilidad) |
| Script de verificacion | `scripts/verify-module.sh` | Saber heuristico (ejecucion declarada) |
| Salida real de `mvn test` | `evidencia/salida-mvn-test.log` | Saber axiologico (no afirmar pruebas no ejecutadas) |
| Reporte Surefire | `evidencia/surefire-ProductoTest.txt` | Saber axiologico (trazabilidad) |
| Este documento | `evidencia/P01_EVIDENCIA.md` | Saber axiologico (comunicacion profesional) |

## Identificador del commit final

```
eb10d99e41830e72c4ae9490f4115ac90bf2efe1
```

Este es el commit final ya subido al repositorio remoto (ver seccion
siguiente). Corresponde al ultimo de una secuencia de commits que documentan
el incremento de forma trazable: creacion de `Producto` y pruebas, ajuste
del metodo `descontar` y las excepciones al contrato exacto de
`M01_lectura_estudiante`, y la incorporacion de la carpeta `evidencia/`.

## URL del repositorio

```
https://github.com/byDiegoRV/P01_RoldanVivanco_DiegoNoe
```

El push ya se realizo y se confirmo con `git log -1 --format="%H"` en la
maquina del estudiante, obteniendo exactamente el commit indicado arriba.
Verificar en una ventana de incognito que la URL es accesible sin sesion
iniciada antes de reportarla en Eminus.

## Ambiente en el que se ejecuto la verificacion

- Sistema: Windows 11 10.0 (amd64).
- JDK: 25.0.2 (Oracle Corporation, 64-bit). El `pom.xml` fija
  `<release>11</release>` como objetivo de compilacion; un JDK mas nuevo
  compila igual hacia esa release (ver discrepancia registrada mas abajo).
- Maven: **PENDIENTE** — falta pegar aqui la salida real de `mvn -version`
  ejecutada en esta misma maquina Windows.

> **Nota de consistencia:** la seccion "Comando ejecutado y resultado real"
> de abajo todavia muestra la salida de `java -version` / `javac -version`
> / `mvn -version` obtenida en el entorno de preparacion original (Ubuntu,
> OpenJDK 11.0.31, Maven 3.8.7), no en esta maquina Windows. Se deja
> **marcado como PENDIENTE** en vez de sustituirlo por un dato inventado:
> reemplazar ese bloque por la salida real de los tres comandos corridos en
> Windows antes de la entrega final, para que todo el documento sea
> consistente con el ambiente declarado arriba.

## Comando ejecutado y resultado real

Comando declarado en la guia: `mvn clean test`, ejecutado desde la raiz de
`tcsw-ventas/`.

Resultado real observado (ver `evidencia/salida-mvn-test.log` y
`evidencia/surefire-ProductoTest.txt` para la salida completa):

```
Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Registro de versiones efectivas — **PENDIENTE de actualizar con la salida
real en Windows** (lo de abajo corresponde al entorno de preparacion
original, no a esta maquina):

```
$ java -version
openjdk version "11.0.31" 2026-04-21
OpenJDK Runtime Environment (build 11.0.31+11-post-1ubuntu1-24.04.2-Ubuntu)
OpenJDK 64-Bit Server VM (build 11.0.31+11-post-1ubuntu1-24.04.2-Ubuntu, mixed mode, sharing)

$ javac -version
javac 11.0.31

$ mvn -version
Apache Maven 3.8.7
Java version: 11.0.31, vendor: Ubuntu, runtime: /usr/lib/jvm/java-11-openjdk-amd64
```

Discrepancia registrada: el perfil de ejecucion declarado en P01/M01 pide
Maven 3.9.9; ademas, la maquina real usada por el estudiante (Windows, JDK
25.0.2) difiere del entorno de preparacion original (Ubuntu, JDK 11.0.31).
Ambas discrepancias se dejan por escrito en vez de ocultarlas, siguiendo el
saber axiologico de M01 ("honestidad al distinguir lo ejecutado de lo
supuesto").

## Declaracion de resultados (VERIFICADO / NO_VERIFICADO / PENDIENTE)

- **VERIFICADO** — Compilacion del codigo fuente (`main` y `test`) con Java 11.
- **VERIFICADO** — Ejecucion de las 17 pruebas JUnit 5 de `ProductoTest`
  (6 casos positivos, 8 casos negativos o limite y 3 pruebas dedicadas a
  comprobar que un rechazo no modifica el estado), todas en verde.
- **VERIFICADO** — Encapsulamiento de `Producto`: los atributos son
  privados y las mutaciones (`actualizarPrecio`, `agregarExistencia`,
  `descontar`) validan toda precondicion antes de modificar el objeto y
  estan cubiertas por prueba, incluyendo la distincion entre
  `IllegalArgumentException` (argumento invalido) e `IllegalStateException`
  (operacion imposible por el estado actual) que exige el ejemplo de M01.
- **VERIFICADO** — Push al repositorio remoto: el codigo esta publicado en
  `https://github.com/byDiegoRV/P01_RoldanVivanco_DiegoNoe` y el commit
  final coincide con el declarado arriba.
- **NO_VERIFICADO** — El goal `clean` del plugin `maven-clean-plugin` no
  pudo ejecutarse en el entorno de preparacion original (sandbox) por una
  dependencia transitiva (`plexus-utils:1.1`) ausente en su repositorio
  local Debian sin acceso a internet completo; esto es una limitacion de
  ese entorno de preparacion, no del proyecto. Como equivalente funcional
  se elimino manualmente el directorio `target/` antes de compilar y
  probar. **Falta confirmar si `mvn clean test` corre sin este problema en
  la maquina Windows real** — de ser asi, esta declaracion puede subirse a
  VERIFICADO.
- **PENDIENTE** — Verificacion de la imagen Docker (`docker build`): el
  `Dockerfile` se entrega listo para usarse, pero no se ha construido
  todavia. Debe verificarse con Docker disponible.
- **PENDIENTE** — Completar el registro de versiones (`java -version`,
  `javac -version`, `mvn -version`) con la salida real de la maquina
  Windows, para reemplazar el bloque heredado del entorno de preparacion
  original (ver nota de consistencia arriba).

## Manejo responsable de datos

No se incluyeron credenciales, datos personales, rutas privadas de este
entorno ni contenido docente en el repositorio. Confirmar que el commit
final este atribuido con el nombre y correo reales del estudiante
(`git config user.name` / `user.email`), no con datos de ejemplo.

## Continuidad hacia P02

Este repositorio es acumulativo: el mismo commit final `eb10d99` es el
punto de partida sobre el que se construyo **P02. Venta en memoria**
(entidades `Venta`, `DetalleVenta` y el objeto de valor `Dinero`, ver
`evidencia/P02_EVIDENCIA.md`). La trazabilidad entre ambas entregas se
sostiene en que P02 parte exactamente de este commit sin reescribir el
historial de P01.