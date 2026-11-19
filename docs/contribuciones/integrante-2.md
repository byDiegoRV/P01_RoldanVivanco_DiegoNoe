
---

## P10 — Cliente Angular (M13)

**Integrante:** Xochitl Salgado (`xochitlsalgado`)
**Rama:** `feature/p10-xochitl`

| Issue | Trabajo realizado | Estado |
|---|---|---|
| #56 (P10-05) | 5 pruebas de `aErrorApi` (error de la API, detalles vacíos, sin conexión, respuesta desconocida y sin filtrar detalles internos) | VERIFICADO (27 de 27 SUCCESS) |
| #57 (P10-06) | Reproducción de la guía desde cero en su equipo, correcciones a la guía y registro de errores | VERIFICADO (`MODULO_M13_VERIFICADO` en su equipo) |

**VERIFICADO:** pruebas de `errores-api` y 27 de 27 pruebas del cliente en su equipo, arranque de la API y del cliente con el catálogo cargando datos de la API, y `./scripts/verify-module.sh M13` (`MODULO_M13_VERIFICADO`).
**NO_VERIFICADO:** recorrido de venta con folio y error sin conexión en su equipo (sin evidencia propia registrada; esos casos están verificados en el equipo de Noe, `P10-04_*.png`), cobertura, Sonar del cliente y pruebas de extremo a extremo automatizadas.
**PENDIENTE:** PR y entrega.
## C03 — Integración web (Corte 3, R04)

**Integrante:** Xochitl Salgado (`xochitlsalgado`)
**Rama:** `feature/c03-xochitl` (issue #65, PR #68)

### Qué hice

| Issue | Trabajo | Archivos principales | Criterio R04 |
|---|---|---|---|
| #65 C03-04 | Reproduje el corte en un segundo equipo (Windows 11, Java 23, Node 24.21.0, npm 11.19.0): pruebas del backend y de Angular, verificación oficial M13, persistencia con reinicio de la API y casos de éxito (detalle), de error (nombre inválido) y de estado vacío | `scripts/evidencia-c03-pruebas.sh`, `evidencia/C03/C03-04_*`, `docs/registro-errores-c03.md` | API, Angular, Errores y validación, Calidad |

El cierre del corte (#66) lo realizó Noe por falta de disponibilidad de tiempo mía; su detalle está en `docs/contribuciones/integrante-1.md`.

### Decisiones y observaciones

- Las salidas de las pruebas se generan con un script que quita rutas del equipo y colores, para que la evidencia no contenga datos personales.
- Los resultados de persistencia se copiaron con nombre propio (`C03-04_*_xochitl.txt`) y se restauraron los archivos de Noe, para no pisar su evidencia.
- La reproducción se hizo con una versión de Java distinta (23) a la de Noe (11); el proyecto compila y pasa las 142 pruebas con ambas.
- Los errores reales que aparecieron están en `docs/registro-errores-c03.md` (terminal equivocada para los comandos de Bash y una instalación de dependencias lenta).

### Pruebas y evidencia

- Backend: 142 pruebas, `BUILD SUCCESS`. Angular: 34 pruebas en SUCCESS. `./scripts/verify-module.sh M13`: `MODULO_M13_VERIFICADO`.
- Archivos: `evidencia/C03/C03-04_*_xochitl.txt` y capturas `evidencia/C03/capturas/C03-04_*_xochitl.png`.

### Estado

**VERIFICADO:** pruebas del backend y de Angular, verificación M13, persistencia y reinicio, casos de éxito (detalle), de error (nombre inválido) y de estado vacío.
**NO_VERIFICADO:** análisis de Sonar sobre el código TypeScript.
