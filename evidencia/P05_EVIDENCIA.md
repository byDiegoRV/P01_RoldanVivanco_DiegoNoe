# P05 — Arquitectura Hexagonal: Dominio y Puertos

**Experiencia educativa:** Tecnologías para la Construcción de Software  
**Actividad:** P05 — Implementación de Arquitectura Hexagonal (Dominio y Puertos)  

## Descripción de lo realizado
Se refactorizó el módulo `tcsw-ventas` para adoptar los principios de la arquitectura hexagonal, aislando la lógica de negocio y definiendo las fronteras del sistema.

* **Estructuración de Paquetes:** Creación de directorios para aislar el núcleo (`domain/`) y los puertos de entrada (`application/ports/in/`) y salida (`application/ports/out/`).
* **Definición de Puertos:** 
  * `RegistrarVentaUseCase` (Puerto de Entrada): Define el contrato para procesar y registrar ventas.
  * `ProductoRepository` y `VentaRepository` (Puertos de Salida): Establecen la abstracción para la persistencia de datos.
* **Refactorización del Dominio:** Traslado de las entidades de negocio (`Producto`, `Venta`, `DetalleVenta`) al paquete `domain/` actualizando sus respectivos paquetes e imports.
* **Verificación y Pruebas:** Ejecución exitosa de 44 pruebas unitarias en verde (0 errores) y validación de calidad con SonarQube.

## Ambiente
* Windows 11, JDK 11.0.32, Apache Maven 3.9.13.
* Visual Studio Code y Git Bash.
* SonarQube (Análisis local).