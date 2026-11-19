# Registro de errores — C03 (reproducción en el segundo equipo)

Errores reales que aparecieron al reproducir el corte en el equipo de Xochitl. Cada fila describe algo que ocurrió; no se agregan errores que no se hayan presentado. Sin rutas personales ni contraseñas.

Si no apareció ningún error, deja la tabla vacía y escribe debajo exactamente: "Sin errores al reproducir C03 en el equipo de Xochitl."

| # | Fecha | Quién | Paso | Error o síntoma | Causa | Solución | Estado |
|---|---|---|---|---|---|---|---|
| 1 | 18-nov-2026 | Xochitl | Cargar el `.env` y correr scripts | `source` y `set -a` no se reconocen | Los comandos de Bash se escribieron en una terminal PowerShell | Abrir Git Bash (en VS Code, desde el menú de la terminal) | Resuelto |
| 2 | 18-nov-2026 | Xochitl | Script de pruebas (`npm ci`) | La terminal parecía detenida con avisos `npm warn deprecated` | La instalación de dependencias tarda varios minutos en su equipo; los avisos son de paquetes antiguos y no son errores | Esperar a que termine; el script concluyó con `VERIFICADO` | Resuelto |
