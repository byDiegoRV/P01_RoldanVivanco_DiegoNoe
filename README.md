tcsw-ventas — P01/P02/P03
Ambiente reproducible, Producto, Venta y Git
Prototipo de ventas para la Experiencia Educativa Tecnologías para la Construcción de Software (TCSW-19234).
•	P01 — Módulo M01. Ambiente y bases de POO: preparar un ambiente Java reproducible y construir la primera entidad del prototipo, Producto.
•	P02 — Módulo M02. Modelado orientado a objetos: modelar una venta en memoria con objetos colaborantes — DetalleVenta y Venta — sobre el Producto construido en P01.
•	P03 — Módulo M03. Calidad inicial y Git: practicar ramas, integración y resolución explícita de un conflicto real sobre el código existente, agregando el método precioConDescuento a Producto.
________________________________________
Contenido del repositorio
tcsw-ventas/
├── pom.xml
├── Dockerfile
├── scripts/
│   └── verify-module.sh
├── src/
│   ├── main/
│   │   └── java/.../
│   │       ├── Producto.java
│   │       ├── DetalleVenta.java
│   │       └── Venta.java
│   └── test/
│       └── java/.../
│           ├── ProductoTest.java
│           ├── DetalleVentaTest.java
│           └── VentaTest.java
└── README.md
Descripción
Archivo	Descripción
pom.xml	Proyecto Maven con JUnit 5 y JaCoCo
Dockerfile	Ambiente reproducible mediante contenedor
scripts/verify-module.sh	Script de verificación para M01, M02 y M03
Producto.java	Entidad Producto de P01
DetalleVenta.java	Objeto de valor que representa una línea de venta
Venta.java	Entidad que compone los detalles de una venta
ProductoTest.java	Pruebas automatizadas de Producto
DetalleVentaTest.java	Pruebas automatizadas de DetalleVenta
VentaTest.java	Pruebas automatizadas de Venta
________________________________________
1. Requisitos del ambiente
Herramienta	Versión usada / mínima	Verificación
Java (JDK)	11	java -version / javac -version
Maven	3.6+	mvn -version
Git	2.x	git --version
Docker	24.x (opcional)	docker --version
VS Code	Con extensión "Extension Pack for Java" (opcional)	—
El proyecto fija la versión de compilación en el pom.xml mediante:
<release>11</release>
Por lo tanto, el ambiente de referencia del proyecto es Java 11.
Perfil de ejecución declarado para P01: Java 11 y Maven 3.9.9.
Registro de versiones efectivas
Para verificar el ambiente se pueden utilizar los siguientes comandos:
java --version
javac -version
mvn --version
Ejemplo del ambiente utilizado:
$ java --version

java 11.0.32 2026-07-21 LTS

Java(TM) SE Runtime Environment 18.9
(build 11.0.32+7-LTS-196)

Java HotSpot(TM) 64-Bit Server VM 18.9
(build 11.0.32+7-LTS-196, mixed mode)

$ javac -version

javac 11.0.32

$ mvn --version

Apache Maven 3.9.13
El proyecto utiliza JDK 11 como versión de referencia.
Maven
El perfil declarado para P01 utiliza Maven 3.9.9. El proyecto utiliza únicamente el ciclo de vida estándar de Maven:
•	Validar
•	Compilar
•	Probar
•	Empaquetar
El pom.xml no depende de características exclusivas de una versión específica de Maven 3.9.x.
________________________________________
2. Clonar el repositorio
git clone https://github.com/byDiegoRV/P01_RoldanVivanco_DiegoNoe
cd tcsw-ventas
________________________________________
3. Compilar y ejecutar las pruebas
Ejecutar:
mvn clean test
Resultado esperado:
BUILD SUCCESS
Además, las pruebas deben finalizar con:
Failures: 0
Errors: 0
Skipped: 0
Verificación por módulo
También puede utilizarse el script de comprobación reproducible:
cd tcsw-ventas

./scripts/verify-module.sh M01
./scripts/verify-module.sh M02
./scripts/verify-module.sh M03
El script:
1.	Registra las versiones efectivas de java, javac y mvn.
2.	Ejecuta mvn clean test.
3.	Comprueba que la construcción termine correctamente.
4.	Imprime MODULO_M01_VERIFICADO, MODULO_M02_VERIFICADO o MODULO_M03_VERIFICADO cuando corresponde.
5.	Si algo falla, imprime MODULO_<MODULO>_NO_VERIFICADO y conserva el error en la salida.
________________________________________
4. Alternativa reproducible con Docker
Para construir la imagen:
docker build -t tcsw-ventas:p01 .
El Dockerfile ejecuta:
mvn clean test package
Como resultado, una construcción exitosa de la imagen confirma que el proyecto puede compilarse y ejecutar sus pruebas dentro de un ambiente limpio y aislado.
________________________________________
5. La entidad Producto
Producto modela un artículo del catálogo de ventas con las siguientes propiedades:
•	codigo — String, no nulo ni vacío.
•	nombre — String, no nulo ni vacío.
•	precio — double, mayor o igual que 0.
•	existencia — int, mayor o igual que 0.
Encapsulamiento
El estado del objeto se protege mediante encapsulamiento:
•	Los atributos son privados.
•	codigo y nombre son inmutables después de la construcción.
•	precio y existencia solo pueden modificarse mediante métodos de negocio.
•	Las operaciones validan sus precondiciones antes de modificar el estado.
Los principales métodos de negocio son:
actualizarPrecio(...)
agregarExistencia(...)
descontar(...)
precioConDescuento(...)
De esta manera, un rechazo nunca deja al objeto en un estado parcialmente modificado.
Excepciones
Para descontar(cantidad) se distinguen las causas del rechazo.
IllegalArgumentException
Se utiliza cuando el argumento proporcionado es inválido.
Ejemplos:
•	cantidad <= 0
•	Código nulo o vacío.
•	Nombre nulo o vacío.
•	Precio negativo.
•	Existencia negativa.
IllegalStateException
Se utiliza cuando el argumento es válido, pero la operación no puede realizarse debido al estado actual del objeto.
Ejemplo:
descontar más unidades que la existencia disponible
________________________________________
6. Modelo de dominio P02: Venta y DetalleVenta
classDiagram
    class Producto {
        -codigo String
        -nombre String
        -precio double
        -existencia int
        +descontar(cantidad)
        +actualizarPrecio(nuevoPrecio)
        +precioConDescuento(porcentaje)
    }

    class DetalleVenta {
        -producto Producto
        -cantidad int
        -precioUnitario double
        +subtotal() double
    }

    class Venta {
        -folio String
        -detalles List~DetalleVenta~
        +agregarPartida(producto, cantidad)
        +calcularTotal() double
    }

    Venta "1" *-- "many" DetalleVenta : compone
    DetalleVenta "many" --> "1" Producto : referencia
DetalleVenta
DetalleVenta es un objeto de valor que representa una línea del ticket.
Contiene:
•	Producto.
•	Cantidad.
•	Precio unitario.
El precio unitario queda congelado en el momento de la venta. Esto evita que un cambio posterior en el precio del catálogo modifique retroactivamente una venta ya realizada.
Dos instancias de DetalleVenta son iguales cuando contienen el mismo:
•	Producto.
•	Cantidad.
•	Precio unitario.
No posee un identificador propio.
Venta
Venta es una entidad con identidad.
Su identidad está determinada por:
folio
Dos ventas con el mismo folio representan la misma entidad, aunque sus detalles puedan ser diferentes.
Venta compone sus objetos DetalleVenta. La lista de detalles pertenece a la venta y se expone como no modificable mediante:
Collections.unmodifiableList(...)
Al ejecutar:
agregarPartida(producto, cantidad)
la venta reutiliza:
Producto.descontar(cantidad)
Esto permite validar la cantidad y la existencia antes de registrar el detalle.
Por lo tanto, una partida rechazada no modifica parcialmente:
•	La existencia del producto.
•	La lista de detalles de la venta.
________________________________________
7. SonarQube — P02
Como parte de P02 se realizó el análisis de calidad utilizando SonarQube Community Edition.
Resultado
MODULO_M02_VERIFICADO
Resultado de SonarQube: VERIFICADO.
Primera ejecución
La primera ejecución detectó:
5 hallazgos Code Smell
0 Bugs
0 Vulnerabilities
Los hallazgos fueron:
•	4 hallazgos Major: relacionados con lambdas de assertThrows que contenían más de una invocación que podía lanzar una excepción. Estos fueron corregidos extrayendo las operaciones necesarias fuera del lambda y dejando una sola invocación dentro de assertThrows.
•	1 hallazgo Info: relacionado con la regla java:S1135, provocado por la detección de la cadena "TODO" dentro de la palabra "todos". Este hallazgo fue justificado como falso positivo, ya que no existía ningún comentario TODO pendiente en el código.
Segunda ejecución
Después de realizar las correcciones se ejecutó nuevamente el análisis:
ANALYSIS SUCCESSFUL
0 issues abiertos
Quality Gate: Passed
Resultado final: VERIFICADO.
El análisis confirmó que los hallazgos detectados inicialmente fueron atendidos y que el proyecto cumplió con el Quality Gate de SonarQube.
________________________________________
8. Ramas y resolución de un conflicto — P03
Para practicar el flujo de trabajo con ramas se agregó el método:
precioConDescuento(double porcentaje)
Este método calcula el precio aplicando un porcentaje de descuento sin modificar el precio real del producto.
Ramas utilizadas
Se creó una rama base:
rama-base
A partir de ella se crearon:
rama-descuento-resta
rama-descuento-factor
Cada rama implementó el mismo método de una manera diferente.
rama-descuento-resta
Calcula primero el descuento y posteriormente lo resta del precio:
precio - descuento
Además, valida que el porcentaje se encuentre entre:
0 y 100
rama-descuento-factor
Utiliza un factor multiplicador:
precio * (1 - porcentaje / 100)
Esta versión inicialmente no validaba correctamente el límite superior de 100.
Conflicto
Al fusionar las dos ramas sobre rama-base, Git generó un conflicto real en:
Producto.java
con los marcadores:
<<<<<<<
=======
>>>>>>>
Resolución
Se conservó la implementación de rama-descuento-resta, debido a que validaba correctamente el porcentaje máximo permitido.
La implementación de rama-descuento-factor tenía un defecto: con un porcentaje mayor a 100, por ejemplo:
150%
el resultado podía ser negativo.
Ambas fórmulas producen el mismo resultado matemático para porcentajes válidos, por lo que la decisión se tomó para conservar la validación correcta.
Después de resolver el conflicto:
•	Se ejecutaron nuevamente las pruebas.
•	Se agregaron pruebas para precioConDescuento.
•	Se verificaron los límites 0 y 100.
•	Se verificaron los porcentajes inválidos.
Historial
El historial resultante se puede consultar mediante:
git log --graph --oneline --all
Ejemplo:
*   <commit> (rama-base) Agrega pruebas para precioConDescuento...
*   <commit> Resuelve conflicto en precioConDescuento:
|           conserva validación de límite superior (0-100)
|\
| * <commit> (rama-descuento-factor)
|           Agrega precioConDescuento (versión factor) en Producto
* | <commit> (rama-descuento-resta)
|           Agrega precioConDescuento (versión resta) en Producto
|/
* <commit> (master) ...
El punto de entrega final de P03 queda marcado con:
v0.3.0
sobre master, después de fusionar rama-base.
________________________________________
9. Pruebas automatizadas
El proyecto utiliza JUnit 5.
ProductoTest
Incluye casos positivos como:
•	Creación válida.
•	Precio en cero.
•	Existencia en cero.
•	Actualización de precio.
•	Alta de existencia.
•	Descuento de existencia dentro de los límites permitidos.
•	precioConDescuento con un porcentaje normal.
•	precioConDescuento(0).
•	precioConDescuento(100).
También incluye casos negativos:
•	Código nulo o vacío.
•	Nombre nulo.
•	Precio negativo.
•	Existencia negativa.
•	Actualización con precio negativo.
•	Descuento con cantidad no positiva.
•	Descuento mayor que la existencia disponible.
•	Alta de existencia con cantidad no positiva.
•	Porcentaje de descuento negativo.
•	Porcentaje de descuento mayor que 100.
También existen pruebas para comprobar que, después de una operación rechazada, el estado del objeto permanece sin modificaciones.
________________________________________
DetalleVentaTest
Incluye pruebas para:
•	Creación válida.
•	Cálculo del subtotal.
•	Precio unitario igual a cero.
•	Congelamiento del precio unitario.
•	Igualdad entre detalles con los mismos datos.
•	Producto nulo.
•	Cantidad cero.
•	Cantidad negativa.
•	Precio unitario negativo.
________________________________________
VentaTest
Incluye pruebas para:
•	Venta vacía con total igual a cero.
•	agregarPartida.
•	Descuento de existencia del producto.
•	Registro del detalle.
•	Congelamiento del precio.
•	Cálculo correcto de varias partidas.
•	Igualdad de ventas con el mismo folio.
•	Folio nulo o vacío.
•	Producto nulo.
•	Cantidad no positiva.
•	Cantidad superior a la existencia disponible.
•	Preservación del estado después de un rechazo.
•	Encapsulamiento de la lista de detalles.
getDetalles() devuelve una lista no modificable. Intentar modificarla desde fuera genera:
UnsupportedOperationException
Resultado de pruebas
El proyecto cuenta con:
44 pruebas
Distribuidas de la siguiente manera:
22 ProductoTest
9  DetalleVentaTest
13 VentaTest
Todas las pruebas fueron verificadas correctamente.
________________________________________
10. Análisis estático con SonarQube — P03
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=< sqa_bf12a565ff78538d340bc701b3728b811ecfaac4 >
El token debe ser personal y no debe incluirse en el repositorio.
Análisis inicial
En P03, la primera ejecución presentó:
Quality Gate: Failed
La condición que falló fue:
Coverage on New Code is less than 80.0%
El proyecto todavía no tenía configurado un reporte de cobertura, por lo que SonarQube no podía determinar correctamente el porcentaje de código nuevo cubierto por pruebas.
Para solucionarlo se agregó:
jacoco-maven-plugin
al pom.xml.
El reporte generado se encuentra en:
target/site/jacoco/jacoco.xml
Esta es una de las rutas que SonarQube busca para obtener información de cobertura.
Resultado final
Después de ejecutar nuevamente el análisis con el reporte de cobertura disponible:
Quality Gate: Passed
All conditions passed
Además:
0 Bugs
0 Vulnerabilities
0 Security Hotspots nuevos
El análisis terminó correctamente:
ANALYSIS SUCCESSFUL
Resultado: VERIFICADO.
________________________________________
11. Bitácora de verificación
P01
Ambiente
JDK 11.0.31
Apache Maven 3.8.7
Git 2.43.0
El perfil declarado para P01 utiliza:
Java 11
Maven 3.9.9
Comando
mvn test
El objetivo clean de mvn clean test no pudo completarse en el entorno de preparación debido a una dependencia transitiva ausente en el repositorio Maven local y a la falta de acceso completo a Internet.
Como equivalente funcional se eliminó manualmente target/ antes de compilar.
Resultado obtenido:
Tests run: 17
Failures: 0
Errors: 0
Skipped: 0
Estado: VERIFICADO
El detalle específico de este punto se encuentra documentado en:
evidencia/P01_EVIDENCIA.md
El paso específico de clean queda declarado como NO_VERIFICADO.
Docker
El Dockerfile se entrega como ambiente reproducible adicional, pero no fue construido en el entorno restringido utilizado para preparar la entrega debido a la ausencia de un daemon Docker.
Estado: PENDIENTE de verificación en un ambiente con Docker disponible.
________________________________________
P02
Ambiente
JDK 11
Windows 11
Apache Maven
SonarQube Server 9.9.8.100196
Docker
Comandos
Pruebas:
mvn clean test
Análisis de calidad:
mvn clean verify \
  org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.projectKey=tcsw-ventas \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<TOKEN>
Resultado
MODULO_M02_VERIFICADO
Pruebas:
Tests run: 39
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
Estado: VERIFICADO
SonarQube
Primera ejecución:
5 hallazgos Code Smell
0 Bugs
0 Vulnerabilities
Se corrigieron:
4 hallazgos Major
relacionados con lambdas de assertThrows que contenían más de una invocación.
El hallazgo restante:
1 hallazgo Info
fue justificado como falso positivo, debido a la detección de "TODO" dentro de la palabra "todos".
Segunda ejecución:
ANALYSIS SUCCESSFUL
0 issues abiertos
Quality Gate: Passed
Resultado de SonarQube: VERIFICADO.
________________________________________
P03
Ambiente
JDK 11.0.32
Windows 11
Apache Maven 3.9.13
SonarQube Server 9.9.8.100196
Docker
Pruebas
Se ejecutó:
mvn clean test
en:
rama-base
y posteriormente en:
master
después de la fusión.
Resultado
Tests run: 44
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
Estado: VERIFICADO
Conflicto de Git
El conflicto de ramas fue provocado y resuelto de forma real mediante Git utilizando los marcadores:
<<<<<<<
=======
>>>>>>>
La resolución se documenta en la sección correspondiente de P03.
Estado: VERIFICADO
SonarQube
La primera ejecución presentó:
Quality Gate: Failed
debido a que no se había configurado previamente el reporte de cobertura.
Después de agregar JaCoCo y ejecutar nuevamente el análisis:
Quality Gate: Passed
0 issues nuevos
Estado: VERIFICADO
Ramas y etiqueta
Las ramas utilizadas fueron:
rama-base
rama-descuento-resta
rama-descuento-factor
Finalmente:
master
fue actualizado mediante la fusión y etiquetado con:
v0.3.0
Estado: VERIFICADO
________________________________________
12. Resumen final
El proyecto tcsw-ventas integra los entregables correspondientes a P01, P02 y P03:
Módulo / herramienta	Descripción	Estado
P01	Ambiente Java 11 y entidad Producto	✅ Verificado
P02	Venta y DetalleVenta	✅ Verificado
P02 — SonarQube	5 hallazgos iniciales, correcciones y Quality Gate Passed	✅ Verificado
P03	Ramas, conflicto, descuento y calidad	✅ Verificado
JUnit 5	Pruebas automatizadas	✅ 44 pruebas
JaCoCo	Cobertura de código	✅ Configurado
SonarQube	Análisis estático	✅ Quality Gate Passed
Git	Ramas y resolución de conflicto	✅ Verificado
Versión de Java
El proyecto utiliza Java 11 como versión de referencia y compilación:
<release>11</release>
Las verificaciones documentadas utilizan JDK 11.
________________________________________
Estado general
P01  → VERIFICADO
P02  → VERIFICADO
P03  → VERIFICADO

Java → 11
Tests → 44/44
SonarQube → Quality Gate Passed
Git → Conflicto provocado y resuelto
Tag → v0.3.0

