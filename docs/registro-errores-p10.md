# Registro de errores — P10 (cliente Angular)

Errores reales encontrados durante el trabajo de P10, con su causa y su solución. Cada fila describe algo que ocurrió; no se agregan errores que no se hayan presentado.

| # | Fecha | Quién | Contexto | Error o síntoma | Causa | Solución | Estado |
|---|---|---|---|---|---|---|---|
| 1 | 9-nov-2026 | Noe | Análisis de Sonar del backend | 21 pruebas con `Define la variable de entorno DB_PASSWORD`; Maven omite el análisis | El `.env` no estaba cargado en la terminal usada | `set -a; source .env; set +a` en la misma terminal antes de Maven | Resuelto |
| 2 | 9-nov-2026 | Noe | Edición de `tsconfig.app.json` | Aviso del editor sobre `rootDir` | Versión de TypeScript del editor distinta a la del proyecto | Se ignoró: `ng build` compila sin errores y no se modificó el archivo | Resuelto (no era un error del proyecto) |
| 3 | 9-nov-2026 | Noe | Primera ejecución del catálogo | El catálogo mostró "No se pudo conectar con el servidor..." | La API no estaba corriendo en el puerto 9098 | Iniciar la API; el cliente ofrece **Reintentar** | Resuelto (comportamiento esperado del manejo de errores) |

## Errores al reproducir la guía (segunda integrante)

Registra aquí cada error que aparezca al seguir `docs/guia-reproduccion-p10.md` en tu equipo. Para cada uno anota el comando, el mensaje (sin rutas personales ni contraseñas), la causa y cómo lo resolviste. Si no apareció ningún error, escribe exactamente eso.

| # | Fecha | Quién | Paso de la guía | Error o síntoma | Causa | Solución | Estado |
|---|---|---|---|---|---|---|---|
| 4 | | | | | | | |
