# Contribución — Xochitl Salgado

**Tareas de C02:** C02-05 y C02-06 (issues #5 y #6 según los mensajes de sus commits; confirmar el número real en GitHub).

> Borrador elaborado a partir del historial de Git. Xochitl debe revisarlo y completar lo que falte antes de la entrega.

## Qué hizo
- **C02-05:** `scripts/verify-module.sh` con soporte para M11: exige las variables `DB_*`, reinicia PostgreSQL desde cero, recrea el esquema, comprueba las tablas y ejecuta `mvn clean test`, sin imprimir credenciales. Ejecutó `mvn test` contra PostgreSQL: 112 pruebas, 0 fallos (`docs/evidencia/C02-05_mvn_test_ok.txt`).
- **C02-06:** guía de reproducción de C02 en el README y estados VERIFICADO, NO_VERIFICADO y PENDIENTE.
- Añadió `VentaFrameTest` en `src/test/java/mx/uv/tcsw/ventas/`.

## Commits
c1234da, 16d1505, e5645a1 (PR #40).

## Revisión cruzada
<completar: PR revisado (partes 01 y 02 del PR #39), fecha y comentarios>.

## Dificultades
<completar>.
