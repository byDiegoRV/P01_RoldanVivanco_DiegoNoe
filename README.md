# tcsw-ventas — P01. Ambiente reproducible y Producto

Prototipo de ventas para la Experiencia Educativa **Tecnologias para la
Construccion de Software** (TCSW-19234). Este entregable corresponde a la
actividad **P01** del modulo **M01. Ambiente y bases de POO**: preparar un
ambiente Java 11 reproducible y construir la primera entidad del prototipo,
`Producto`.

## Contenido del repositorio

```
tcsw-ventas/
├── pom.xml                     Proyecto Maven (Java 11, JUnit 5)
├── Dockerfile                  Ambiente reproducible por contenedor
├── scripts/verify-module.sh    Script de verificacion (mvn clean test)
├── src/main/java/.../Producto.java       Entidad Producto (encapsulada)
├── src/test/java/.../ProductoTest.java   Pruebas positivas y negativas
└── README.md
```

## 1. Requisitos del ambiente

| Herramienta | Version usada / minima | Verificacion |
|---|---|---|
| Java (JDK)  | 11 | `java -version` / `javac -version` |
| Maven       | 3.6+ | `mvn -version` |
| Git         | 2.x | `git --version` |
| Docker      | 24.x (opcional, para el `Dockerfile`) | `docker --version` |
| VS Code     | con extension "Extension Pack for Java" (opcional) | — |

El proyecto fija la version de compilacion en el `pom.xml`
(`<release>11</release>`), por lo que basta con tener un JDK 11 o superior
compatible con esa release.

**Perfil de ejecucion declarado para P01:** Java 11 y Maven 3.9.9.

### Registro de versiones efectivas (comprobacion real, no supuesta)

`java`, `javac` y `mvn` responden preguntas distintas: `java` identifica el
runtime invocado, `javac` confirma el compilador y `mvn` informa el Java con
el que corre el propio proceso de Maven. Cualquier discrepancia se registra
en vez de ocultarse. Salida obtenida al preparar esta entrega:

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

**Discrepancia registrada:** el perfil declarado para P01 pide Maven 3.9.9;
el ambiente de preparacion usado para verificar esta entrega tenia
disponible Maven 3.8.7. Ambas versiones ejecutan el mismo ciclo de vida
estandar (validar, compilar, probar, empaquetar) y el `pom.xml` no depende
de caracteristicas exclusivas de 3.9.x, por lo que la construccion es
equivalente; aun asi, quien reproduzca este proyecto con Maven 3.9.9 deberia
confirmarlo con su propio `mvn -version` antes de reportar un resultado.

## 2. Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd tcsw-ventas
```

(Sustituye `<URL_DEL_REPOSITORIO>` por la URL entregada en Eminus.)

## 3. Compilar y ejecutar las pruebas

```bash
mvn clean test
```

Resultado esperado: `BUILD SUCCESS` y un resumen con **0 fallas y 0
errores** sobre las pruebas de `ProductoTest`.

También puede usarse el script de comprobacion reproducible que exige la
guia de la actividad:

```bash
cd tcsw-ventas
./scripts/verify-module.sh M01
```

El script registra las versiones efectivas de `java`, `javac` y `mvn`,
ejecuta `mvn clean test` y, si la construccion termina en `BUILD SUCCESS`,
imprime la linea `MODULO_M01_VERIFICADO`. Si algo falla, imprime
`MODULO_M01_NO_VERIFICADO` y conserva el error en la salida — nunca declara
un resultado que no se ejecuto realmente.

## 4. Alternativa reproducible con Docker

```bash
docker build -t tcsw-ventas:p01 .
```

El build de la imagen ejecuta `mvn clean test package` como parte del
`Dockerfile`, de forma que una imagen construida con exito ya certifica que
el proyecto compila y las pruebas pasan en un ambiente limpio y aislado.

## 5. La entidad `Producto`

`Producto` modela un articulo del catalogo de ventas con:

- `codigo` (String, no nulo ni en blanco)
- `nombre` (String, no nulo ni en blanco)
- `precio` (double, ≥ 0)
- `existencia` (int, ≥ 0)

El estado se protege mediante **encapsulamiento**: los atributos son
privados; `codigo` y `nombre` son inmutables tras la construccion; `precio`
y `existencia` solo pueden modificarse a traves de metodos de negocio
(`actualizarPrecio`, `agregarExistencia`, `descontar`) que validan toda
precondicion **antes** de modificar el objeto, de modo que un rechazo nunca
deja el estado parcialmente modificado. Esto evita que el objeto quede en un
estado inconsistente (precios o existencias negativas, codigos vacios,
etc.).

Siguiendo el contrato descrito en M01 para `descontar(cantidad)`, la clase
distingue el tipo de excepcion segun la causa del rechazo:

- `IllegalArgumentException` — el argumento en si mismo es invalido
  (por ejemplo, `cantidad <= 0`, o los datos del constructor).
- `IllegalStateException` — el argumento es valido pero la operacion es
  imposible por el estado actual del objeto (por ejemplo, descontar mas
  cantidad de la existencia disponible).

## 6. Pruebas automatizadas

`ProductoTest` (JUnit 5) incluye:

- **Casos positivos**: creacion valida, precio/existencia en cero,
  actualizacion de precio, alta de existencia y descuento de existencia
  dentro de los limites permitidos.
- **Casos negativos / limite**: rechazo de codigo nulo o en blanco, nombre
  nulo, precio negativo, existencia negativa, actualizacion de precio con
  valor negativo, descuento con cantidad no positiva (`IllegalArgumentException`),
  descuento mayor que la existencia disponible (`IllegalStateException`) y
  alta de existencia con cantidad no positiva.
- **Preservacion de estado tras un rechazo**: tres pruebas dedicadas
  confirman explicitamente que, tras un `descontar` o `actualizarPrecio`
  rechazado, `getExistencia()` / `getPrecio()` no cambiaron — no basta con
  recibir la excepcion esperada, se comprueba que el objeto no quedo
  parcialmente modificado.

En total, **17 pruebas**, todas verificadas en verde antes de esta entrega.

## 7. Bitacora de verificacion (saber axiologico)

- Ambiente usado para verificar esta entrega: JDK 11.0.31 (OpenJDK),
  Apache Maven 3.8.7 (perfil declarado: 3.9.9 — ver discrepancia registrada
  en la seccion 1), Git 2.43.0.
- Comando ejecutado: `mvn test` (compilacion de `main` y `test`, mas
  ejecucion de Surefire). El goal `clean` de `mvn clean test` no pudo
  completarse en el entorno de preparacion por una dependencia transitiva
  ausente en su repositorio Maven local sin acceso pleno a internet; como
  equivalente funcional se elimino manualmente `target/` antes de compilar.
  Vease `evidencia/P01_EVIDENCIA.md` para el detalle declarado como
  **NO_VERIFICADO** de ese paso especifico.
- Resultado real obtenido: `Tests run: 17, Failures: 0, Errors: 0, Skipped: 0`
  — **VERIFICADO**.
- El `Dockerfile` se entrega como ambiente reproducible adicional pero **no
  se construyo** en el entorno restringido usado para preparar esta entrega
  (sin acceso a un daemon Docker) — declarado como **PENDIENTE** de
  verificacion por quien clone el repositorio con Docker disponible.
- No se incluyen credenciales, datos personales ni rutas privadas en este
  repositorio.
