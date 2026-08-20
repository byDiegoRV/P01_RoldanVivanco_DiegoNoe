# P01 — Archivo breve de evidencia

**Experiencia educativa:** Tecnologias para la Construccion de Software (TCSW-19234)
**Actividad:** P01 — Ambiente reproducible y Producto
**Modulo:** M01. Ambiente y bases de POO
**Fecha de preparacion:** 2026-08-20

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
3cd8a351186251f60e48d16b1da7893b80fe33c0
```

## URL del repositorio

Este proyecto se preparo y verifico en un entorno de trabajo local con Git
inicializado; **no se realizo push a un remoto** desde este entorno (el
entorno de preparacion no tiene acceso a servicios de alojamiento Git como
GitHub). Antes de la entrega en Eminus, el estudiante debe:

1. Crear un repositorio remoto (por ejemplo en GitHub).
2. Agregarlo como `origin` y hacer push de la rama `master`:
   ```bash
   git remote add origin <URL_DEL_REPOSITORIO_REMOTO>
   git push -u origin master
   ```
3. Reportar en Eminus la URL resultante y confirmar que el identificador de
   commit final coincide con el indicado arriba (`3cd8a35...fe33c0`).

## Ambiente en el que se ejecuto la verificacion

- Sistema: contenedor Linux (Ubuntu 24.04) usado como entorno de preparacion.
- JDK: OpenJDK 11.0.31 (Temurin), configurado como `release` de compilacion en `pom.xml`.
- Maven: Apache Maven 3.8.7.
- Git: 2.43.0.

## Comando ejecutado y resultado real

Comando declarado en la guia: `mvn clean test`, ejecutado desde la raiz de
`tcsw-ventas/`.

Resultado real observado (ver `evidencia/salida-mvn-test.log` y
`evidencia/surefire-ProductoTest.txt` para la salida completa):

```
Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Declaracion de resultados (VERIFICADO / NO_VERIFICADO / PENDIENTE)

- **VERIFICADO** — Compilacion del codigo fuente (`main` y `test`) con Java 11.
- **VERIFICADO** — Ejecucion de las 13 pruebas JUnit 5 de `ProductoTest`
  (5 casos positivos, 8 casos negativos o limite), todas en verde.
- **VERIFICADO** — Encapsulamiento de `Producto`: los atributos son
  privados y las mutaciones (`actualizarPrecio`, `agregarExistencia`,
  `retirarExistencia`) validan sus invariantes y son cubiertas por prueba.
- **NO_VERIFICADO** — El goal `clean` del plugin `maven-clean-plugin` no
  pudo ejecutarse en este entorno de preparacion particular por una
  dependencia transitiva (`plexus-utils:1.1`) ausente en el repositorio
  local Debian usado como espejo sin acceso a internet completo; esto es
  una limitacion del entorno de preparacion, no del proyecto. Como
  equivalente funcional se elimino manualmente el directorio `target/`
  antes de compilar y probar, logrando el mismo efecto practico. En un
  ambiente con acceso normal a Maven Central, `mvn clean test` funciona
  sin este obstaculo.
- **PENDIENTE** — Verificacion de la imagen Docker (`docker build`): el
  `Dockerfile` se entrega listo para usarse, pero no se construyo en este
  entorno de preparacion por no contar con acceso a un daemon Docker ni al
  registro de imagenes. Debe verificarse por quien clone el repositorio con
  Docker disponible.
- **PENDIENTE** — Push al repositorio remoto y verificacion de accesibilidad
  publica de la URL (ver seccion anterior).

## Manejo responsable de datos

No se incluyeron credenciales, datos personales, rutas privadas de este
entorno ni contenido docente en el repositorio. El unico dato de contacto
usado para configurar Git localmente es un correo de ejemplo
(`estudiante@example.com`), que debe sustituirse por los datos reales del
autor antes de la entrega final si se desea que el commit refleje su
autoria.
