# Contribución P09 · Diego Noe Roldán Vivanco (integrante 1)

## Issues
- #42 P09-01: base Spring Boot y verificación M12.
- #43 P09-02: endpoints, DTOs y validación.
- #44 P09-03: errores consistentes.
- #45 P09-04: contrato OpenAPI.
- #48 P09-07: calidad, evidencia y cierre (con Xochitl).

## Qué hice
- Base Spring Boot 2.7.18 sobre Java 11, `ApiApplication` y `ConfiguracionApi`, y soporte de `M12` en `scripts/verify-module.sh`.
- Endpoints `GET /api/productos`, `GET /api/productos/{id}` y `POST /api/ventas` con DTOs, casos de uso y validación.
- `ManejadorErrores` con un único formato de error y los códigos 400, 404, 405, 409, 415 y 500.
- Contrato `docs/openapi.yaml`.
- Pruebas de servicio y de controladores (MockMvc), y corrección de los 7 hallazgos de Sonar.
- Evidencia de ejecución real contra PostgreSQL en `evidencia/P09/` e índice `evidencia/P09_EQUIPO_06.md`.
- Revisión del PR #50 de Xochitl.
- Integré en el PR de cierre el script `scripts/curl-p09.sh` y la sección P09 del README, que la integrante 2 completó después de fusionado el PR #50.


## Decisiones técnicas
- Spring Boot 2.7 en lugar de 3 por Java 11.
- Sin importar el BOM de Spring Boot, para no forzar versiones que rompen Hibernate 6.4.4.
- `ConfiguracionApi` con `@Profile("!test")` para que las pruebas HTTP no necesiten base de datos.

