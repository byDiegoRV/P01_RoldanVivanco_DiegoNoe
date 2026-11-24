# H-03 — La integración continua se bloquea en las pruebas de Swing (C04-03)

- **Hecho observado:** en la primera ejecución del flujo de CI, el job `backend` quedó 26 min en `Pruebas y cobertura (mvn verify)`; el último mensaje del log fue `Running ...swing.controlador.VentasControllerTest`. Las 142 pruebas pasan en el equipo de desarrollo en segundos. (Captura: `evidencia/C04/capturas/C04-03_ci_fallida.png`.)
- **Hipótesis:** H1) una prueba abre un `JOptionPane` modal y nadie lo cierra en el *runner*. H2) falta un gestor de ventanas o foco con la pantalla virtual (Xvfb). H3) conexión a la BD sin respuesta.
- **Prueba discriminante:**
  1. `grep` en `src/main` y `src/test`: `VentasController` abre `JOptionPane.showMessageDialog` (líneas 101, 111, 123, 151, 156, 207, 221), pero `VentasControllerTest` no tiene ningún cierre automático (solo `VentasControllerPersistenteTest` lo tiene).
  2. `pom.xml` fuerza `java.awt.headless=false` en Surefire: las pruebas abren ventanas reales.
  3. Al ejecutar `mvn test -Dtest=VentasControllerTest` en el equipo de desarrollo aparecen cuadros de diálogo en pantalla; las pruebas terminan en 14,6 s porque una persona los cierra.
  4. Descarta H3: las pruebas anteriores de la misma ejecución, incluidas las de vista, pasaron con la BD disponible; el bloqueo ocurre en una prueba sin BD (`VentasControllerTest` usa vistas, no repositorios).
- **Resultado:** H1 confirmada: las pruebas dependían de que una persona cerrara los diálogos.
- **Corrección:** extensión de JUnit `CierraDialogosModales` (`src/test/java/mx/uv/tcsw/ventas/`), autodetectada con `junit-platform.properties` y `META-INF/services`, que cierra un diálogo modal solo si lleva más de 1 s visible (para no competir con `VentasControllerPersistenteTest`, que lee el mensaje). No se modificó ninguna prueba existente ni código de producción. Además el flujo tiene `timeout-minutes` y guarda un volcado de hilos (`jstack`) si las pruebas pasan de 10 min.
- **Retest vertical:** `mvn -B verify` local (142 pruebas) y ejecución de CI con los tres jobs en verde.
- **Límite:** las pruebas de Swing siguen necesitando una pantalla (real o virtual); en CI se usa Xvfb.
