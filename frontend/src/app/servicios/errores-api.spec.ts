import { HttpErrorResponse } from '@angular/common/http';

import { aErrorApi } from './errores-api';

describe('aErrorApi', () => {
  it('conserva el error con el formato de la API (409 existencia insuficiente)', () => {
    const respuesta = new HttpErrorResponse({
      status: 409,
      error: {
        status: 409,
        code: 'EXISTENCIA_INSUFICIENTE',
        message: 'Existencia insuficiente para Laptop',
        details: ['productoId=1'],
      },
    });

    const resultado = aErrorApi(respuesta);

    expect(resultado.code).toBe('EXISTENCIA_INSUFICIENTE');
    expect(resultado.message).toBe('Existencia insuficiente para Laptop');
    expect(resultado.details).toEqual(['productoId=1']);
  });

  it('usa una lista vacía de detalles cuando la API no los envía', () => {
    const respuesta = new HttpErrorResponse({
      status: 404,
      error: { status: 404, code: 'PRODUCTO_NO_ENCONTRADO', message: 'Producto no encontrado' },
    });

    expect(aErrorApi(respuesta).details).toEqual([]);
  });

  it('convierte la falta de conexión (status 0) en SIN_CONEXION', () => {
    const respuesta = new HttpErrorResponse({ status: 0, error: new ProgressEvent('error') });

    const resultado = aErrorApi(respuesta);

    expect(resultado.code).toBe('SIN_CONEXION');
    expect(resultado.message).toContain('No se pudo conectar');
  });

  it('convierte una respuesta que no tiene el formato de la API en ERROR_DESCONOCIDO', () => {
    const respuesta = new HttpErrorResponse({ status: 500, error: 'Internal Server Error' });

    expect(aErrorApi(respuesta).code).toBe('ERROR_DESCONOCIDO');
  });

  it('no expone detalles internos de un error inesperado', () => {
    const resultado = aErrorApi(new Error('detalle interno secreto'));

    expect(resultado.code).toBe('ERROR_DESCONOCIDO');
    expect(resultado.message).not.toContain('secreto');
  });
});
