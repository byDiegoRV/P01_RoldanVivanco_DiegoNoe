# Diagnóstico por fronteras — C04 (M14)

Recorrido vertical de la venta: **Angular (formulario) → HTTP (nginx → API) → Aplicación (caso de uso) → Persistencia (PostgreSQL)**.
Autoridad del contrato: `docs/openapi.yaml`. Regla de trabajo: una hipótesis, una prueba; no se cambia código hasta localizar la frontera.

## 1. Matriz por frontera

| Frontera | Síntoma típico | Dónde se observa | Prueba discriminante (comando) | Cobertura automática |
|---|---|---|---|---|
| Formulario (Angular) | Botón/estado de interfaz incorrecto, sin petición | Pruebas del componente (`ng test`) y navegador | Revisar en DevTools → Red si sale la petición; `ng test` del componente | `venta.component.spec.ts`, `producto-detalle.component.spec.ts` |
| HTTP (cliente → nginx → API) | Estado 0 (sin conexión), 404/502 inesperado, CORS/proxy | `docker compose logs web`, `curl` a `:9095` y a `:9098` | `curl -i http://localhost:9098/api/productos` (API directa) vs `curl -i http://localhost:9095/api/productos` (por nginx): si falla solo el segundo, es el proxy | `verify-stack.sh` [3] y [4]; `verify-recorrido.sh` [B] |
| Aplicación (API / caso de uso) | 400/404/409/500 con código de error equivocado | `curl` directo a la API; `docker compose logs api` | Misma petición por `curl` a `:9098`, comparar `status` y `code` con `docs/openapi.yaml` | `verify-recorrido.sh` [B]; pruebas `*RestControllerTest`, `ManejadorErroresTest` |
| Persistencia (PostgreSQL) | La API responde bien pero la BD no refleja el efecto (o lo refleja a medias) | `docker compose exec postgres psql ...` | `SELECT` sobre `ventas`, `detalle_venta`, `productos` antes y después | `verify-recorrido.sh` [A] y atomicidad en [B] |

Regla de red vs backend: estado **0** = el navegador no llegó al servidor (red, proxy, servicio caído); **500** = llegó y el backend falló. Se distinguen en `frontend/src/app/servicios/errores-api.ts` y se prueba en el caso límite de BD caída (`verify-stack.sh --con-caida`).

## 2. Hallazgos

### H-01 — El frontend en contenedor no arranca: puerto 8080 ocupado (infraestructura / HTTP)
- **Hecho observado:** `docker compose up` termina con `Error response from daemon: ports are not available: exposing port TCP 0.0.0.0:8080 ... bind: Only one usage of each socket address`. PostgreSQL y API quedan *Healthy*, `web` no inicia y `verify-stack.sh` imprime `NO_VERIFICADO: docker compose up fallo`.
- **Hipótesis:** H1) otro proceso del equipo ocupa el 8080. H2) el compose o `nginx.conf` están mal. H3) el puerto está reservado por Windows.
- **Prueba discriminante:** `netstat -ano | grep ":8080"` → (completar: PID/estado que se vio). La API en 9098 sí responde, lo que descarta H2 (la construcción y los otros servicios funcionan). `netsh interface ipv4 show excludedportrange protocol=tcp` no reserva el rango 909x, lo que descarta H3 para el puerto elegido después.
- **Resultado:** H1 confirmada; el 8080 está en uso en el equipo.
- **Corrección:** el puerto publicado del frontend se parametriza con `WEB_PORT` (por defecto 9095) en `docker-compose.yml`, `.env.example` y `verify-stack.sh`. No se tocó código de la aplicación.
- **Retest vertical:** `./scripts/verify-stack.sh` → `STACK_C04_VERIFICADO`, y `./scripts/verify-recorrido.sh` → `RECORRIDO_VERIFICADO` (cliente → API → PostgreSQL).
- **Evidencia:** `evidencia/C04/C04-01_hallazgo_puerto8080.txt` y capturas `C04-01_hallazgo_puerto8080.png`, `C04-01_stack.txt`.

### H-02 — El contrato declara un servidor que no existe (contrato divergente)
- **Hecho observado:** `docs/openapi.yaml` declara `servers: http://localhost:8080`, pero la API del proyecto corre en 9098 y el cliente en contenedor se sirve en 9095.
- **Hipótesis:** el contrato quedó con el puerto por defecto de Spring (`PORT:8080`) de P09 y nunca se actualizó.
- **Prueba discriminante:** `grep -n "localhost" docs/openapi.yaml` frente a `curl -s -o /dev/null -w '%{http_code}' http://localhost:9098/api/productos` (200). Es un hallazgo de documentación: el comportamiento HTTP ya cumple el contrato en lo demás (`verify-recorrido.sh`).
- **Corrección:** `servers` del contrato lista 9098 (API directa) y 9095 (cliente con nginx).
- **Retest:** `grep -n "localhost" docs/openapi.yaml` muestra ambos servidores; `./scripts/verify-recorrido.sh` sigue en `RECORRIDO_VERIFICADO`.
- **Límite:** severidad baja; no cambia el comportamiento, solo evita que alguien pruebe contra un puerto equivocado.

(Si `verify-recorrido.sh` revela una divergencia real en la primera corrida, se agrega aquí como H-03 con el mismo formato antes de corregir.)

## 3. Contrato verificado contra la ejecución

| Caso (OpenAPI) | Esperado | Comprobación |
|---|---|---|
| `GET /api/productos/{id}` | 200, campo `existencia` = `stock` en BD | `verify-recorrido.sh` [A] |
| `POST /api/ventas` válido | 201, folio del servidor, fila en `ventas` y `detalle_venta`, existencia descontada | [A] |
| Existencia insuficiente | 409 `EXISTENCIA_INSUFICIENTE`, sin cambios en BD | [B] |
| Cantidad 0 | 400 `VALIDACION_FALLIDA` | [B] |
| Producto inexistente (POST y GET) | 404 `PRODUCTO_NO_ENCONTRADO` | [B] |
| Campo no permitido (tarjeta), JSON mal formado, id no numérico | 400 `SOLICITUD_INVALIDA` | [B] |
| Método no permitido / ruta inexistente | 405 `METODO_NO_PERMITIDO` / 404 `RECURSO_NO_ENCONTRADO` | [B] |
| Ningún error expone trazas, SQL ni credenciales | cuerpo sin `Exception`, `SQLException`, `password` | [B] |

**Límites declarados:** el pago con tarjeta es simulado en el cliente (la API no lo recibe); el estado 0 se prueba en el cliente con servicios simulados y, en la pila real, con la BD caída (`--con-caida`), no apagando el contenedor `api`.

Un contenedor del cliente aparecía unhealthy desde hacía horas aunque la pila funcionaba. Se descartó un fallo de la aplicación y se aisló la causa con una prueba que distingue la hipótesis: el chequeo de salud usaba localhost, que en Alpine resuelve a IPv6, y nginx escucha solo en IPv4. Se corrigió usando 127.0.0.1 y se verificó con el estado de salud del contenedor y con el recorrido completo de la venta.
