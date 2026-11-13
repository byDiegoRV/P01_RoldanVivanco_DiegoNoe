import { HttpErrorResponse } from '@angular/common/http';

import { ErrorApi } from '../modelos/api.modelos';

export const MENSAJE_SIN_CONEXION =
  'No se pudo conectar con el servidor. Verifica que la API esté en ejecución.';
export const MENSAJE_GENERICO = 'Ocurrió un error inesperado. Intenta de nuevo más tarde.';

function esErrorApi(valor: unknown): valor is ErrorApi {
  if (typeof valor !== 'object' || valor === null) {
    return false;
  }
  const candidato = valor as Partial<ErrorApi>;
  return typeof candidato.code === 'string' && typeof candidato.message === 'string';
}

/** Convierte cualquier error de HTTP al formato de error de la API, sin exponer detalles internos. */
export function aErrorApi(error: unknown): ErrorApi {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) {
      return { status: 0, code: 'SIN_CONEXION', message: MENSAJE_SIN_CONEXION, details: [] };
    }
    if (esErrorApi(error.error)) {
      return { ...error.error, details: error.error.details ?? [] };
    }
    return { status: error.status, code: 'ERROR_DESCONOCIDO', message: MENSAJE_GENERICO, details: [] };
  }
  return { status: 0, code: 'ERROR_DESCONOCIDO', message: MENSAJE_GENERICO, details: [] };
}
