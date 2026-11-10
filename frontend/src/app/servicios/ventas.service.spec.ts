import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ErrorApi, VentaPeticion, VentaRespuesta } from '../modelos/api.modelos';
import { VentasService } from './ventas.service';

describe('VentasService', () => {
  let servicio: VentasService;
  let http: HttpTestingController;
  const peticion: VentaPeticion = { partidas: [{ productoId: 1, cantidad: 1 }] };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    servicio = TestBed.inject(VentasService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('registra una venta con POST /api/ventas y devuelve folio y total', () => {
    const esperada: VentaRespuesta = {
      folio: 'V-20261109120000000',
      total: 15000,
      partidas: [
        { productoId: 1, nombre: 'Laptop', cantidad: 1, precioUnitario: 15000, subtotal: 15000 },
      ],
    };
    let recibida: VentaRespuesta | undefined;

    servicio.registrar(peticion).subscribe((venta) => (recibida = venta));

    const solicitud = http.expectOne('/api/ventas');
    expect(solicitud.request.method).toBe('POST');
    expect(solicitud.request.body).toEqual(peticion);
    solicitud.flush(esperada, { status: 201, statusText: 'Created' });
    expect(recibida).toEqual(esperada);
  });

  it('convierte un 409 de existencia insuficiente al formato de error de la API', () => {
    let error: ErrorApi | undefined;

    servicio.registrar(peticion).subscribe({ error: (e: ErrorApi) => (error = e) });

    http.expectOne('/api/ventas').flush(
      {
        status: 409,
        code: 'EXISTENCIA_INSUFICIENTE',
        message: 'No hay existencia suficiente para el producto con id 1.',
        path: '/api/ventas',
        details: [],
      },
      { status: 409, statusText: 'Conflict' },
    );
    expect(error?.status).toBe(409);
    expect(error?.code).toBe('EXISTENCIA_INSUFICIENTE');
    expect(error?.message).toContain('existencia');
  });

  it('conserva el detalle por campo de un 400 de validación', () => {
    let error: ErrorApi | undefined;

    servicio.registrar(peticion).subscribe({ error: (e: ErrorApi) => (error = e) });

    http.expectOne('/api/ventas').flush(
      {
        status: 400,
        code: 'VALIDACION_FALLIDA',
        message: 'La petición contiene datos inválidos.',
        details: ['partidas[0].cantidad: debe ser mayor o igual a 1'],
      },
      { status: 400, statusText: 'Bad Request' },
    );
    expect(error?.code).toBe('VALIDACION_FALLIDA');
    expect(error?.details).toEqual(['partidas[0].cantidad: debe ser mayor o igual a 1']);
  });

  it('no expone detalles internos ante una respuesta que no es del formato de la API', () => {
    let error: ErrorApi | undefined;

    servicio.registrar(peticion).subscribe({ error: (e: ErrorApi) => (error = e) });

    http.expectOne('/api/ventas').flush('<html>fallo interno</html>', {
      status: 500,
      statusText: 'Internal Server Error',
    });
    expect(error?.code).toBe('ERROR_DESCONOCIDO');
    expect(error?.message).not.toContain('html');
  });
});
