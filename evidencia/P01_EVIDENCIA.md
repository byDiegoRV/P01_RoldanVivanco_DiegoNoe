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

Es el commit final que ya subi al repositorio remoto. Junta todo lo que se
pedia: la clase `Producto` con sus pruebas, el ajuste del metodo
`descontar` para que distinga bien entre `IllegalArgumentException` e
`IllegalStateException`, y la carpeta `evidencia/`.

## URL del repositorio

```
https://github.com/byDiegoRV/P01_RoldanVivanco_DiegoNoe
```

Ya hice el push y confirme con `git log -1 --format="%H"` que el commit de
arriba es el que quedo en el remoto. Tambien lo revise en una ventana de
incognito para asegurarme de que se puede abrir sin iniciar sesion.

## Ambiente en el que se ejecuto la verificacion

- Sistema: Windows 11 (amd64).
- JDK: 11.0.32 (Oracle Corporation, 64-bit), version LTS.
- Maven: Apache Maven 3.9.13.

Nota sobre esto: al principio tenia instalado el JDK 25, y el `pom.xml`
compilaba hacia `release 11` sin problema (un JDK mas nuevo puede compilar
hacia una version anterior). Despues cambie a instalar directamente el JDK
11 en mi maquina, asi que ahora el ambiente ya coincide exactamente con lo
que pide la practica, sin necesidad de esa traduccion de version.

## Comando ejecutado y resultado real

Comando declarado en la guia: `mvn clean test`, ejecutado desde la raiz de
`tcsw-ventas/`.

Resultado real observado (ver `evidencia/salida-mvn-test.log` y
`evidencia/surefire-ProductoTest.txt` para la salida completa):

```
Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Registro de versiones efectivas, tal como pide M01_lectura_estudiante:

```
$ java --version
java 11.0.32 2026-07-21 LTS
Java(TM) SE Runtime Environment 18.9 (build 11.0.32+7-LTS-196)
Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.32+7-LTS-196, mixed mode)

$ javac --version
javac 11.0.32

$ mvn --version
Apache Maven 3.9.13 (39d686bd50d8e054301e3a68ad44781df6f80dda)
Maven home: C:\Users\vivar\Downloads\apache-maven-3.9.13-bin\apache-maven-3.9.13
Java version: 11.0.32, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk-11.0.32
Default locale: es_MX, platform encoding: Cp1252
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

Discrepancia registrada: el perfil de ejecucion declarado en P01/M01 pide
Maven 3.9.9, y en mi maquina quedo instalado el 3.9.13. Lo dejo anotado en
vez de ocultarlo, aunque no afecta el resultado: ambas versiones corren el
mismo ciclo de vida de Maven y el proyecto no depende de nada exclusivo de
una version en particular.

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
  (operacion imposible por el estado actual) que pide el ejemplo de M01.
- **VERIFICADO** — Push al repositorio remoto: el codigo esta publicado en
  `https://github.com/byDiegoRV/P01_RoldanVivanco_DiegoNoe` y el commit
  final coincide con el declarado arriba.
- **PENDIENTE** — Verificacion de la imagen Docker (`docker build`): el
  `Dockerfile` esta listo para usarse pero no lo he construido todavia.

## Manejo responsable de datos

No incluí credenciales, datos personales de terceros ni rutas privadas en
el repositorio. Confirme que el commit final este atribuido con mi nombre
y correo reales (`git config user.name` / `user.email`), no con datos de
ejemplo.

## Continuidad hacia P02

Este repositorio es acumulativo: el mismo commit final `eb10d99` es el
punto de partida sobre el que se construyo P02 (Venta en memoria),
agregando las clases `Venta` y `DetalleVenta`.