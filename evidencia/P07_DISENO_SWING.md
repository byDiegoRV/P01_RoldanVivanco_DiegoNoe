# Diseño de Interfaz y Recorrido de Usuario - Cliente Swing (P07)

## 1. Patrón Arquitectónico (MVC)
La interfaz gráfica se construirá utilizando Java Swing (sin dependencias de JavaFX) y aislada en la capa de infraestructura para no contaminar el dominio.
* **Modelo (View-Model):** Clases de transporte de datos exclusivas para la vista (ej. `ProductoViewModel`).
* **Vista:** Componentes gráficos `JFrame` y `JPanel` que solo se encargan de dibujar la interfaz.
* **Controlador:** Listeners y `SwingWorker` que capturan los eventos de la vista, los envían a los casos de uso en segundo plano, y actualizan la vista con el resultado.

## 2. Recorrido del Usuario (User Journey)
El flujo principal de la aplicación constará de las siguientes transiciones:
1. **Inicio - Catálogo:** El usuario abre la aplicación y visualiza la lista de productos disponibles (`CatalogoView`).
2. **Selección:** El usuario selecciona un producto y hace clic en "Agregar al carrito". 
3. **Revisión - Carrito:** El usuario navega al `CarritoView` para ver el resumen de su selección y el total a pagar.
4. **Captura - Formulario:** El usuario presiona "Pagar", abriendo el `FormularioVentaView` para ingresar sus datos.
5. **Confirmación:** Si los datos son válidos, se confirma la compra y el sistema regresa al estado inicial.

## 3. Estados de la Interfaz
Para garantizar una buena experiencia y evitar bloqueos (EDT), la interfaz manejará los siguientes estados:
* **Estado de Carga (Loading):** Mientras el `SwingWorker` procesa la lógica de negocio, se deshabilitarán los botones y se mostrará un indicador visual.
* **Estado de Error:** Si la validación de entrada falla (ej. campos vacíos) o hay un error en el controlador, se mostrará un mensaje claro al usuario sin ocultar el fallo.
* **Estado de Éxito:** Notificación visual al completar la operación correctamente.