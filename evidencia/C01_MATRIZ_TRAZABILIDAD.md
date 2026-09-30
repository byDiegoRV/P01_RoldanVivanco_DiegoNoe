# Matriz de Trazabilidad y Contribuciones (C01)

| Requisito / Criterio (R02) | Componente / Archivo Asociado | Responsable | Estado de Verificación |
| :--- | :--- | :--- | :--- |
| **Arquitectura Hexagonal** | `src/main/java/mx/uv/tcsw/ventas/` (domain, application, adapter) | Diego Noé (Backend/Integración) | **VERIFICADO** (M07 Success) |
| **Patrones: Strategy y Factory** | `CalculadorDescuentoStrategy.java`, `PagoFactory.java` | Diego Noé (Backend/Integración) | **VERIFICADO** |
| **Pruebas Ejecutables** | `src/test/java/mx/uv/tcsw/ventas/` (Suite JUnit 5) | Xóchitl (QA/Testing) | **VERIFICADO** (100% Pass) |
| **Documentación y Diagramas** | `evidencia/diagramas/`, `C01_ARQUITECTURA_Y_PATRONES.md` | Miranda (Documentación) | **VERIFICADO** |
| **Guía de Reproducción** | `README.md` | Miranda (Documentación) | **VERIFICADO** |
| **Etiqueta del Repositorio** | Tag: `v0.1-arquitectura` | Diego Noé (Backend/Integración) | **VERIFICADO** |

## Evidencia de Pruebas Ejecutables
Las reglas de negocio fueron validadas exitosamente.
* Comando de ejecución: `mvn clean test`
* Resultado: Todas las pruebas unitarias en estado exitoso (Build Success). Se incluye el log de salida en `evidencia/salida-mvn-test.log`.