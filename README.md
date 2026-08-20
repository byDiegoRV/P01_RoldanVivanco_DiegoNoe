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

También puede usarse el script auxiliar mencionado en la guia de la
actividad:

```bash
cd tcsw-ventas
./scripts/verify-module.sh M01
```

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
(`actualizarPrecio`, `agregarExistencia`, `retirarExistencia`) que validan
cada operacion y lanzan `IllegalArgumentException` cuando se viola un
invariante. Esto evita que el objeto quede en un estado inconsistente
(precios o existencias negativas, codigos vacios, etc.).

## 6. Pruebas automatizadas

`ProductoTest` (JUnit 5) incluye:

- **Casos positivos**: creacion valida, precio/existencia en cero,
  actualizacion de precio, alta y baja de existencia dentro de los limites
  permitidos.
- **Casos negativos / limite**: rechazo de codigo nulo o en blanco, nombre
  nulo, precio negativo, existencia negativa, actualizacion de precio con
  valor negativo, retiro de mas existencia de la disponible y alta de
  existencia con cantidad no positiva.

En total, **13 pruebas**, todas verificadas en verde antes de esta entrega.

## 7. Bitacora de verificacion (saber axiologico)

- Ambiente usado para verificar esta entrega: JDK 11.0.31 (Temurin/OpenJDK),
  Apache Maven 3.8.7, Git 2.43.0.
- Comando ejecutado: `mvn clean test` (fase `test`, que incluye compilacion
  de `main` y `test`).
- Resultado real obtenido: `Tests run: 13, Failures: 0, Errors: 0, Skipped: 0`
  — **VERIFICADO**.
- El `Dockerfile` se entrega como ambiente reproducible adicional pero **no
  se ejecuto** en el entorno restringido usado para preparar esta entrega
  (sin acceso a Docker Hub) — declarado como **PENDIENTE** de verificacion
  por quien clone el repositorio con acceso a internet completo.
- No se incluyen credenciales, datos personales ni rutas privadas en este
  repositorio.
