import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ErrorApi, Producto } from '../modelos/api.modelos';
import { ProductosService } from './productos.service';

describe('ProductosService', () => {
  let servicio: ProductosService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    servicio = TestBed.inject(ProductosService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('lista los productos con GET /api/productos', () => {
    const esperados: Producto[] = [
      { id: 1, codigo: 'P-001', nombre: 'Laptop', precio: 15000, existencia: 8 },
    ];
    let recibidos: Producto[] = [];

    servicio.listar().subscribe((productos) => (recibidos = productos));

    const peticion = http.expectOne('/api/productos');
    expect(peticion.request.method).toBe('GET');
    peticion.flush(esperados);
    expect(recibidos).toEqual(esperados);
  });

  it('convierte un 404 al formato de error de la API', () => {
    let error: ErrorApi | undefined;

    servicio.obtener(999).subscribe({ error: (e: ErrorApi) => (error = e) });

    http.expectOne('/api/productos/999').flush(
      {
        status: 404,
        code: 'PRODUCTO_NO_ENCONTRADO',
        message: 'No existe el producto con id 999.',
        path: '/api/productos/999',
        details: [],
      },
      { status: 404, statusText: 'Not Found' },
    );
    expect(error?.status).toBe(404);
    expect(error?.code).toBe('PRODUCTO_NO_ENCONTRADO');
    expect(error?.message).toBe('No existe el producto con id 999.');
  });

  it('indica falta de conexión cuando el servidor no responde', () => {
    let error: ErrorApi | undefined;

    servicio.listar().subscribe({ error: (e: ErrorApi) => (error = e) });

    http.expectOne('/api/productos').error(new ProgressEvent('error'));
    expect(error?.code).toBe('SIN_CONEXION');
    expect(error?.status).toBe(0);
  });
});
