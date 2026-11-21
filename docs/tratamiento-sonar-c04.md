# Tratamiento de hallazgos SonarQube — candidato v1.0-rc (C04)

Herramienta: SonarQube 9.9.8 (local, `http://localhost:9000`). Proyecto: `mx.uv.tcsw:tcsw-ventas` (backend, Java 11). Evidencia: `evidencia/C04/C04-04_sonar_previo.txt`, `..._final.txt` y capturas en `evidencia/C04/capturas/`.

## 1. Resultados
| Métrica | Análisis de C03 (19-nov) | C04 previo | C04 final |
|---|---|---|---|
| Quality Gate | Passed | OK (Passed) | (completar tras el análisis final) |
| Bugs / Vulnerabilidades / Hotspots | (completar) | 0 / 0 / (completar con captura) | (completar) |
| Code smells | (completar) | 1 (MAJOR) | (completar; se espera 0) |
| Cobertura (código completo) | (completar) | (completar) | (completar) |
| Duplicación | (completar) | (completar) | (completar) |
| Commit analizado | (completar) | 28408d7 | (completar: commit de la corrección) |

## 2. Tratamiento de cada hallazgo abierto
Decisiones permitidas: **Corregido** (con commit) · **Aceptado con justificación** (marcado en SonarQube como "Won't fix" / "Accepted", con motivo) · **Pendiente** (con causa y plan).

| # | Regla | Severidad | Archivo:línea | Decisión | Justificación / commit |
|---|---|---|---|---|---|
| 1 | java:S2925 ("Thread.sleep" no debe usarse en pruebas) | MAJOR · Code Smell | `src/test/java/mx/uv/tcsw/ventas/CierraDialogosModales.java:60` | **Corregido** | La extensión de JUnit creada en C03/C04 (H-03) vigilaba los diálogos con un hilo y `Thread.sleep`. Se reemplazó por un `ScheduledExecutorService` con `scheduleWithFixedDelay`, sin cambiar el comportamiento (se volvió a probar el cierre automático). Commit: (completar) |

Origen del hallazgo: lo introdujo la propia corrección de CI de C04-03; el análisis lo detectó antes de la entrega. Si un análisis no muestra hallazgos abiertos, se declara así: "0 hallazgos abiertos en el commit X; no hay nada que tratar", y se justifica el Quality Gate en vez de inventar correcciones.

## 3. Frontend (TypeScript)
Intento acotado a 45 minutos con el escáner oficial en Docker (proyecto `mx.uv.tcsw:tcsw-ventas-frontend`).
- Resultado: **(VERIFICADO / NO_VERIFICADO — completar)**
- Si NO_VERIFICADO: causa observada (completar) y límite declarado: el análisis de Sonar cubre solo el backend; el cliente queda cubierto por `ng build`, `ng test` y la CI.

## 4. Límites declarados
- SonarQube es local: no corre en la integración continua (ver `docs/integracion-continua.md`).
- La cobertura de Sonar proviene de JaCoCo del backend; la del frontend no se integra salvo que el intento de TypeScript lo logre.

Frontend (TypeScript): VERIFICADO (proyecto mx.uv.tcsw:tcsw-ventas-frontend, escáner oficial 5.0.1 en contenedor con Node 18.20.1, commit (completar)).

Diagnóstico del primer intento: el análisis se detenía indefinidamente en el sensor «TypeScript analysis». Hecho: el último paso registrado en modo depuración era «Deploying eslint-bridge» sobre el directorio montado desde Windows. Hipótesis descartadas con prueba: versión de Node (la imagen trae 18.20.1, compatible), memoria y CPU (7,8 GB y 12 CPU) y alcance de red (el contenedor llega a SonarQube). Hipótesis comprobada: escritura muy lenta de miles de archivos en el montaje. Corrección: -Dsonar.working.directory=/tmp/scannerwork. Retest: análisis exitoso en 19,7 s.

| # | Proyecto | Regla | Severidad | Archivo | Decisión | Justificación |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 2 | frontend | CSS vacío (*Unexpected empty source*) | Major · Code smell | `src/app/app.css:1` | Corregido | El componente raíz no usa estilos propios; se eliminó el archivo y la referencia `styleUrl`. |
| 3 | frontend | Tabla sin descripción (accesibilidad, WCAG 2 A) | Minor · Bug | `catalogo.component.html:27` | Corregido | `<caption class="sr-only">`: la interfaz no cambia y la tabla queda descrita para lectores de pantalla. |
| 4 | frontend | Igual | Minor · Bug | `venta.component.html:63` | Corregido | Igual (carrito). |

Límites: se analiza una copia sin node_modules (menos tipos resueltos) y la cobertura del cliente no se importa a Sonar; está cubierta por 34 pruebas de Karma y la integración continua.