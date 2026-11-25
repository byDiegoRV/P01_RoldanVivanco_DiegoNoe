# Evidencia C04 — Candidato de liberación (R05) — Equipo 06

Equipo 06: Diego Noe Roldán Vivanco (`byDiegoRV`) y Xochitl Salgado (`xochitlsalgado`).
Repositorio: https://github.com/byDiegoRV/P01_RoldanVivanco_DiegoNoe — rama de entrega: `master` (la rama `main` solo tiene el commit inicial).
Versión: etiqueta `v1.0-rc` (comprobable con `git tag -l v1.0-rc` y la Release de GitHub; el commit del tag es `git rev-parse v1.0-rc^{commit}`).
Verificación oficial: `./scripts/verify-module.sh M14` → `STACK_VERIFICADO` y `MODULO_M14_VERIFICADO`.
Estados: VERIFICADO / NO_VERIFICADO / PENDIENTE. Las capturas están en `evidencia/C04/capturas/`.

## 1. Evidencia mínima solicitada
| Evidencia | Dónde | Estado |
|---|---|---|
| Artefactos | Jar `tcsw-ventas.jar` en la Release `v1.0-rc` y como artefacto `tcsw-ventas-jar` del CI; Dockerfiles y compose en la raíz | PENDIENTE al escribir este índice: el jar se adjunta a la Release al crear la etiqueta |
| Reporte de pruebas | `evidencia/C04/C04-05_reporte_pruebas.txt`, `docs/matriz-riesgos-pruebas.md` | VERIFICADO |
| Instrucciones de despliegue | `README.md`, sección "Candidato de liberación v1.0-rc" | VERIFICADO |
| Plan de reversa | `docs/plan-reversa.md`, `evidencia/C04/C04-06_ensayo_reversa.txt` | VERIFICADO |

## 2. Criterios de R05
### Integración (2) — issues #70, #71
| Evidencia | Estado |
|---|---|
| Pila de tres servicios con healthchecks: `C04-01_stack.txt`, `C04-01_compose_ps.png`, `C04-01_stack_verificado.png`, `C04-01_web_catalogo.png` | VERIFICADO |
| Caída y recuperación de la base de datos: `C04-01_stack_caida.txt`, `C04-01_caida_bd.png` | VERIFICADO |
| Recorrido de venta y M14: `C04-02_recorrido.txt`, `C04-02_verify-m14.txt`, `C04-02_recorrido_verificado.png`, `C04-02_verify_m14.png`, `C04-02_diff_verify-module.txt` | VERIFICADO |
| Diagnóstico por fronteras (H-01, H-02, H-04): `docs/diagnostico-fronteras-c04.md`, `C04-02_openapi_servers.png`, `C04-02_H04_unhealthy.png`, `C04-02_H04_healthy.png` | VERIFICADO |
| Ambos adaptadores de persistencia: `C04-05b_persistencia_modos.txt`, `C04-05b_persistencia_hibernate.png`, `C04-05b_persistencia_jdbc.png`, `C04-05b_persistencia_resultado.png` | VERIFICADO |

### Pruebas (1.5) — issue #74
| Evidencia | Estado |
|---|---|
| 142 pruebas del backend y 34 del frontend, con cobertura: `C04-05_reporte_pruebas.txt`, `C04-05_reporte_backend.png`, `C04-05_reporte_frontend.png`, `C04-05_jacoco_html.png`, `C04-04_frontend_tests_34.png` | VERIFICADO |
| Matriz de riesgos R1 a R16: `docs/matriz-riesgos-pruebas.md`, `C04-05_matriz.png` | VERIFICADO (R1–R12 y R14–R16); R13 PENDIENTE |
| R13: dos ventas simultáneas sobre la misma existencia | PENDIENTE: no se diseñó la prueba de concurrencia; plan en la matriz |

### Sonar (1) — issue #73
| Evidencia | Estado |
|---|---|
| Backend: `C04-04_sonar_previo_*`, `C04-04_sonar_final.txt`, `C04-04_sonar_despues_correciones.png` | VERIFICADO |
| Frontend: `C04-04_sonar_frontend.txt`, `C04-04_sonar_frontend_previo_issues.png`, `C04-04_sonar_frontend_final_issues.png`, `C04-04_sonar_frontend_final_overview.png`, `C04-04_sonar_frontend_terminal.png` | VERIFICADO |
| Tratamiento de hallazgos: `docs/tratamiento-sonar-c04.md` | VERIFICADO |
| Sonar dentro del CI | NO_VERIFICADO: la instancia es local (`localhost:9000`) |

### CI (1) — issue #72
| Evidencia | Estado |
|---|---|
| Workflow `.github/workflows/ci.yml` y registro de ejecuciones: `docs/integracion-continua.md`, `C04-03_ci_verde.png`, `C04-03_ci_pila.png`, `C04-03_ci_artefactos.png`, `C04-03_ci_historial_final.png` | VERIFICADO |
| Hallazgo H-03 (bloqueo por diálogos modales) y su corrección: `docs/hallazgo-h03-ci-bloqueo.md`, `C04-03_ci_fallida.png`, `C04-03_ci_fallida_2.png`, `C04-03_local_sin_dialogos.png` | VERIFICADO |
| CI de `master` tras el merge del PR #78 (ejecución 16, tres jobs en verde) | VERIFICADO |

### Documentación (1)
`README.md` (sección C04), `docs/plan-reversa.md`, `docs/notas-de-version-v1.0-rc.md`, `docs/matriz-riesgos-pruebas.md`, `docs/diagnostico-fronteras-c04.md`, `docs/hallazgos-reproduccion-c04.md`, `docs/contribuciones/integrante-1.md` y `docs/contribuciones/integrante-2.md`. Estado: VERIFICADO.

### Reproducibilidad (1) — issues #70, #76
| Evidencia | Estado |
|---|---|
| Reproducción por otra persona en otro equipo (Xochitl, Windows, JDK 23, PR #79): `C04-07_versiones_xochitl.txt`, `C04-07_m14_xochitl_intento1.txt` (falla E-01), `C04-07_m14_xochitl.txt` (`MODULO_M14_VERIFICADO`), `C04-07_caida_xochitl.txt` | VERIFICADO por salidas de terminal; sin capturas de pantalla |
| Registro de errores de la reproducción: `docs/registro-errores-c04.md` (E-01) y `docs/hallazgos-reproduccion-c04.md` (E-01 a E-03) | VERIFICADO |
| Pruebas del backend con el JDK de Xochitl: `C04-07_backend_xochitl.txt` | NO_VERIFICADO (E-02, falta `DB_PASSWORD`; en CI y en el equipo de Noe pasan) |
| Versiones efectivas del candidato: `C04-01_versiones.txt`, `docs/versiones-congeladas.md`, `.env.example` | VERIFICADO |
| Verificación final del candidato tras el cierre: `C04-08_m14_final.txt` | VERIFICADO |

### Versión (0.5) — issue #77
| Evidencia | Estado |
|---|---|
| Versiones coherentes: backend 1.0.0, cliente 1.0.0, contrato 1.0.0 | VERIFICADO |
| Notas de versión: `docs/notas-de-version-v1.0-rc.md` | VERIFICADO |
| Etiqueta `v1.0-rc` y Release con el jar | PENDIENTE al escribir este índice: el tag no puede estar dentro del commit que señala; se comprueba con `git tag -l` y la Release |

## 3. Contribuciones por integrante
| Integrante | Issues | Documento |
|---|---|---|
| Diego Noe Roldán Vivanco | #70, #71, #72, #73, #74, #75, #77 | `docs/contribuciones/integrante-1.md` |
| Xochitl Salgado | #76 (PR #79, revisado por Noe) | `docs/contribuciones/integrante-2.md` (sección redactada por Noe a partir de la evidencia del PR y declarada así) |

## 4. Límites declarados
- R13 (concurrencia): sin prueba.
- SonarQube corre en local; no hay análisis en el CI.
- Reproducción externa: un solo equipo (Windows); sin capturas de pantalla; sin prueba manual en navegador; backend local NO_VERIFICADO por E-02 (hallazgos E-01, E-02, E-03 en `docs/hallazgos-reproduccion-c04.md`).
- La descripción del PR #79 enlaza por error "run #16" al PR #16 y no incluye `Closes #76`; el issue #76 se cerró manualmente.
- La reversa usa el código de `v0.3-web` con la definición de despliegue del candidato; respaldo puntual (`pg_dump`); ensayo en un solo entorno local.
- Rutas de usuario: algunas evidencias anteriores a C04 (por ejemplo `docs/evidencia/C02-05_mvn_test_ok.txt`) y el historial de Git de C04 contienen rutas locales; en las evidencias de C04 se enmascararon y el historial no se reescribió.
- Los tags `v0.3.0`, `v0.4.0` y `v1.0.0` son de prácticas anteriores; `v1.0.0` (C02) no es este candidato.
- Todo el candidato se verificó con JDK 11 en el equipo de Noe; en el equipo de Xochitl (JDK 23) solo se verificó la pila en contenedores.
