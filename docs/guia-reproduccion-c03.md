# Guía de reproducción — C03 (integración web)

Complementa `docs/guia-reproduccion-p10.md`, que describe los requisitos, la obtención del código y el arranque de la base, la API y el cliente. Aquí se agregan las novedades del Corte 3 y su verificación.

## 1. Requisitos
Los de P10: JDK 11 o superior (probado con 11 y con 23), Maven 3.9, Docker, Node 24, npm 11, Chrome y **Git Bash** (no PowerShell: los scripts usan `source` y otras órdenes de Bash). Hace falta el archivo `.env` descrito en la guía de P10; no se versiona.

## 2. Pruebas y verificación oficial
Con la base arriba (`docker compose up -d`) y sin `npm start` abierto, desde la raíz:
```bash
bash scripts/evidencia-c03-pruebas.sh <etiqueta>
```
Ejecuta `mvn clean verify` (esperado: 142 pruebas, `BUILD SUCCESS`), las pruebas de Angular (esperado: 34 en SUCCESS) y `./scripts/verify-module.sh M13` (esperado: `MODULO_M13_VERIFICADO`). Guarda las salidas en `evidencia/C03/` sin rutas personales. La instalación de dependencias de Angular puede tardar varios minutos sin mostrar avance.

## 3. Recorrido funcional
Con la API en el puerto 9098 y el cliente con `npm start` en `frontend/`:
1. **Catálogo** (`/`): al hacer clic en el nombre de un producto se abre su **detalle** (`/productos/:id`) con código, precio y existencia.
2. **Nueva venta** (`/venta`): agrega productos, escribe un nombre (solo letras, acentos, ñ y espacios) y 16 dígitos de **prueba** (por ejemplo `4111111111111111`) y confirma: aparece el folio y el total.

| Caso | Qué hacer | Resultado esperado |
|---|---|---|
| Producto inexistente | Abrir `/productos/99` | "El producto solicitado no existe." sin botón Reintentar |
| Nombre inválido | Escribir `Ana123` en el pago | "Ingresa un nombre válido (solo letras)." |
| API detenida | Detener la API y recargar el catálogo | "No se pudo conectar con el servidor..." con Reintentar |
| Catálogo vacío | Ver "Evidencia de estado vacío" | "No hay productos disponibles." |

## 4. Persistencia de extremo a extremo
Con la API en marcha:
```bash
bash scripts/evidencia-c03-persistencia.sh
# reiniciar la API y luego:
bash scripts/evidencia-c03-persistencia.sh reinicio
```
Esperado: todas las líneas del resultado dicen `VERIFICADO`. Las consultas se ejecutan dentro del contenedor y no imprimen credenciales.

## 5. Evidencia de estado vacío
`docker compose down -v` **borra los datos** de la base. Después de `docker compose up -d` y de reiniciar la API, el catálogo está vacío. Para restaurar los datos de ejemplo:
```bash
docker exec -i postgres_ventas psql -U "$DB_USER" -d "$DB_NAME" < scripts/datos-demo.sql
```

## 6. Dependencias entre interfaces y núcleo
```bash
bash scripts/evidencia-c03-dependencias.sh despues
```
Muestra que la API y Swing usan el mismo puerto de entrada `VenderProductosUseCase`.

## 7. Problemas frecuentes
| Síntoma | Causa | Solución |
|---|---|---|
| `source` o `set -a` "no se reconocen" | Terminal PowerShell | Usar Git Bash |
| La terminal parece detenida con avisos `npm warn deprecated` | `npm ci` tarda varios minutos | Esperar; no interrumpir |
| `Define la variable de entorno DB_PASSWORD` | `.env` no cargado en esa terminal | `set -a; source .env; set +a` |
| Error de autenticación hacia la base | Volumen con otra contraseña | `docker compose down -v && docker compose up -d` (borra los datos) |

## 8. Qué no cubre esta entrega
- El pago es una simulación de la interfaz.
- Sonar analiza el backend (Java); el código TypeScript no se analiza.
- No hay pruebas de extremo a extremo automatizadas con navegador.
