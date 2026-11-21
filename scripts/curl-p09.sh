#!/usr/bin/env bash
# Demostración manual de la API REST de ventas (P09) con curl.
# Uso:   PORT=9098 ./scripts/curl-p09.sh
#        BASE_URL=http://localhost:9098 ./scripts/curl-p09.sh
#        SIN_VENTA=1 ./scripts/curl-p09.sh   (omite el POST válido: ese descuenta existencia)
# Requisito: la API debe estar corriendo (mvn spring-boot:run).

BASE="${BASE_URL:-http://localhost:${PORT:-8080}}"

if ! curl -s -o /dev/null "$BASE/api/productos"; then
  echo "No se pudo conectar con la API en $BASE. Arráncala con: PORT=9098 mvn spring-boot:run" >&2
  exit 1
fi

# Parámetros: descripción, método, ruta y, opcional, cuerpo JSON
peticion() {
  echo "### $1"
  echo "$2 $3"
  if [ -n "$4" ]; then
    curl -s -i -X "$2" "$BASE$3" -H "Content-Type: application/json" -d "$4"
  else
    curl -s -i -X "$2" "$BASE$3"
  fi
  echo
  echo
}

echo "API: $BASE"
echo

# Casos positivos
peticion "1. Listar productos (200)" GET /api/productos
peticion "2. Consultar un producto (200)" GET /api/productos/1
if [ -z "$SIN_VENTA" ]; then
  peticion "3. Registrar una venta válida (201)" POST /api/ventas \
    '{"partidas":[{"productoId":1,"cantidad":1}]}'
fi

# Casos negativos
peticion "4. Producto inexistente (404)" GET /api/productos/999
peticion "5. Id no numérico (400)" GET /api/productos/abc
peticion "6. Cantidad inválida (400, VALIDACION_FALLIDA)" POST /api/ventas \
  '{"partidas":[{"productoId":1,"cantidad":0}]}'
peticion "7. Campo no permitido (400, SOLICITUD_INVALIDA)" POST /api/ventas \
  '{"campoNoPermitido":"x","partidas":[{"productoId":1,"cantidad":1}]}'
peticion "8. JSON mal formado (400)" POST /api/ventas '{"partidas": ['
peticion "9. Sin existencia suficiente (409)" POST /api/ventas \
  '{"partidas":[{"productoId":1,"cantidad":999999}]}'
peticion "10. Método no permitido (405)" DELETE /api/productos
peticion "11. Ruta inexistente (404)" GET /api/no-existe
