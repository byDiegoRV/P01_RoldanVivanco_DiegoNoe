# Guía de reproducción — P10 (cliente Angular)

Esta guía permite levantar la API de P09 y el cliente Angular de P10 desde cero y repetir la verificación oficial del módulo M13.

## 1. Requisitos

| Herramienta | Versión usada | Comprobación |
|---|---|---|
| JDK | 11 | `java -version` |
| Maven | 3.9 | `mvn -version` |
| Docker Desktop (con Compose) | en ejecución | `docker compose version` |
| Node.js | 24.11.0 | `node -v` |
| npm | 11.6.2 | `npm -v` |
| Angular CLI | 20.3.x (se usa la del proyecto con `npx ng`) | `npx ng version` |
| Google Chrome | cualquier versión reciente (las pruebas usan ChromeHeadless) | — |

Se recomienda **Git Bash** para todos los comandos (en PowerShell no existe `source` y `curl` es un alias distinto).

## 2. Obtener el código

```bash
git clone https://github.com/byDiegoRV/P01_RoldanVivanco_DiegoNoe.git
cd P01_RoldanVivanco_DiegoNoe
git checkout master
```

## 3. Variables de entorno

En la raíz del repositorio crea el archivo `.env` con las variables `DB_USER`, `DB_PASSWORD`, `DB_NAME` y `DB_PORT` (los valores de base de datos y el puerto se indican en el README de P09; la contraseña la eliges tú). **El archivo `.env` no se sube al repositorio.**

## 4. Base de datos y API (puerto 9098)

```bash
docker compose up -d
set -a; source .env; set +a
PORT=9098 mvn spring-boot:run
```

Deja esta terminal abierta. En otra terminal comprueba la API:

```bash
curl -s http://localhost:9098/api/productos
```

Debe responder una lista JSON de productos.

## 5. Cliente Angular

En una terminal nueva:

```bash
cd frontend
npm ci
npm start
```

Abre `http://localhost:4200`. El cliente llama a rutas `/api/...` y el servidor de desarrollo las reenvía a `http://localhost:9098` (archivo `frontend/proxy.conf.json`).

## 6. Recorrido funcional

1. **Catálogo** (`/`): se ve la tabla de productos con código, nombre, precio y existencia. Si no hay productos aparece "No hay productos disponibles."
2. **Nueva venta** (`/venta`): elige un producto, indica la cantidad y pulsa **Agregar**.
3. En **Pago (simulado)** escribe un nombre y 16 dígitos **de prueba** (por ejemplo `4111 1111 1111 1111`). Estos datos no se envían a la API ni se guardan.
4. Pulsa **Confirmar venta**: aparece el folio y el total, el carrito se vacía y la existencia del catálogo baja.

Casos de error que se pueden comprobar:

| Caso | Qué hacer | Resultado esperado |
|---|---|---|
| Cantidad mayor a la existencia | Pedir más piezas de las disponibles | Aviso rojo "Solo hay N pieza(s)..." y no se agrega al carrito |
| Tarjeta inválida | Escribir menos de 16 dígitos | "El número de tarjeta debe tener 16 dígitos." y no se registra la venta |
| API detenida | Detener la API y confirmar una venta (o recargar el catálogo) | "No se pudo conectar con el servidor. Verifica que la API esté en ejecución." |

## 7. Pruebas automáticas

```bash
cd frontend
npx ng build
npx ng test --watch=false --browsers=ChromeHeadless
```

Esperado: compilación sin errores y todas las pruebas en `SUCCESS`.

## 8. Verificación oficial del módulo

Desde la raíz del repositorio, sin `npm start` abierto:

```bash
./scripts/verify-module.sh M13
```

Esperado: la última línea es `MODULO_M13_VERIFICADO`. Si aparece `MODULO_M13_NO_VERIFICADO`, el script conserva el error: revisa la sección siguiente.

## 9. Problemas frecuentes

| Síntoma | Causa | Solución |
|---|---|---|
| Al correr Maven: `Define la variable de entorno DB_PASSWORD` | El `.env` no está cargado en esa terminal | `set -a; source .env; set +a` y volver a ejecutar en la misma terminal |
| El catálogo muestra "No se pudo conectar con el servidor..." | La API no está corriendo en el puerto 9098 | Iniciar la API (paso 4) y pulsar **Reintentar** |
| `connection refused` o error de autenticación hacia la BD | El contenedor de PostgreSQL no está arriba o tiene otra contraseña | `docker compose up -d`; si la contraseña cambió: `docker compose down -v && docker compose up -d` |
| `curl` falla en PowerShell | `curl` es un alias de otro comando | Usar Git Bash o `curl.exe` |
| `npm ci` falla con EPERM | `npm start` u otro proceso tiene bloqueada la carpeta `node_modules` | Detener el servidor de desarrollo y repetir |
| El editor marca un aviso en `tsconfig.app.json` | Versión de TypeScript del editor | No es un error del proyecto; `ng build` es la referencia |

## 10. Qué no cubre esta entrega

- El pago es una simulación del cliente: la API de P09 no procesa pagos y no se modificó.
- El análisis de SonarQube cubre el backend (Java); el código TypeScript no se analiza.
- No hay pruebas de extremo a extremo automatizadas con navegador.
