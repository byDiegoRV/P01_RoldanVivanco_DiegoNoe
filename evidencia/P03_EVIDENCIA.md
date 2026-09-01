# P03 — Archivo breve de evidencia

**Experiencia educativa:** Tecnologias para la Construccion de Software
**Actividad:** P03 — Ramas y conflictos
**Modulo:** M03. Calidad inicial y Git

## URL del repositorio

```
https://github.com/byDiegoRV/P01_RoldanVivanco_DiegoNoe/tree/master
```

Mismo repo de P01 y P02.

## Commit final (en master, tras fusionar y etiquetar)

```
<COMPLETAR: git log -1 --format="%H" en master, despues de la fusion>
```

Tag de esta entrega: `v0.3.0`

## Ambiente

- Windows 11, JDK 11.0.32, Apache Maven 3.9.13.
- SonarQube 9.9.8.100196 en Docker local.

## Que se hizo

Cree una rama base (`rama-base`) y dos ramas de trabajo
(`rama-descuento-resta` y `rama-descuento-factor`), ambas agregando el
mismo metodo `precioConDescuento` a `Producto` pero con implementacion
distinta. Una validaba que el porcentaje no pasara de 100, la otra no.

Al fusionar las dos ramas sobre `rama-base` salio un conflicto real de Git
(marcadores `<<<<<<<`, `=======`, `>>>>>>>`) porque ambas tocaban las
mismas lineas. Lo resolvi quedandome con la version que si valida el
limite superior, porque la otra version podia regresar un precio negativo
si alguien pedia un descuento mayor a 100%.

Despues de resolver agregue 5 pruebas para `precioConDescuento` (caso
normal, los dos limites 0 y 100, y los dos casos invalidos que causaron el
conflicto).

## Pruebas

```
mvn clean test
Tests run: 44, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## SonarQube

Primera corrida: Quality Gate en **Failed** porque no habia reporte de
cobertura configurado (0%, aunque si habia pruebas reales). Agregue el
plugin de JaCoCo al `pom.xml`. Segunda corrida: Quality Gate en
**Passed**, 0 issues nuevos.

## Declaracion de resultados

- **VERIFICADO** — Conflicto provocado y resuelto de verdad (no simulado),
  con la decision documentada arriba y en el README seccion 7.
- **VERIFICADO** — 44 pruebas pasando, incluyendo las nuevas de
  `precioConDescuento`.
- **VERIFICADO** — SonarQube con Quality Gate en Passed tras agregar
  JaCoCo.
- **VERIFICADO** — Ramas `rama-base`, `rama-descuento-resta` y
  `rama-descuento-factor` subidas al repositorio remoto.
- **PENDIENTE** — Fusion final a `master` y tag `v0.3.0` (falta completar
  el commit final arriba una vez hecho esto).
