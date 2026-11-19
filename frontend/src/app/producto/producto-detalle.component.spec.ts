import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { routes } from '../app.routes';
import { ProductoDetalleComponent } from './producto-detalle.component';

describe('ProductoDetalleComponent', () => {
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter(routes), provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  /** Navega con las rutas reales de la aplicación, así también se comprueba la ruta /productos/:id. */
  async function abrir(url: string) {
    const harness = await RouterTestingHarness.create();
    const componente = await harness.navigateByUrl(url, ProductoDetalleComponent);
    return { harness, componente };
  }

  function texto(harness: RouterTestingHarness): string {
    harness.detectChanges();
    return (harness.routeNativeElement as HTMLElement).textContent ?? '';
  }

  it('muestra los datos del producto cuando la API responde', async () => {
    const { harness } = await abrir('/productos/1');
    expect(texto(harness)).toContain('Cargando producto');

    http.expectOne('/api/productos/1').flush({ id: 1, codigo: 'P-001', nombre: 'Laptop', precio: 15000, existencia: 8 });

    const contenido = texto(harness);
    expect(contenido).toContain('Laptop');
    expect(contenido).toContain('P-001');
    expect(contenido).toContain('8 pieza(s)');
    expect(contenido).toContain('Volver al catálogo');
  });

  it('marca como agotado un producto sin existencia', async () => {
    const { harness } = await abrir('/productos/3');
    http.expectOne('/api/productos/3').flush({ id: 3, codigo: 'P-003', nombre: 'Teclado', precio: 1200, existencia: 0 });

    expect(texto(harness)).toContain('Agotado');
  });

  it('muestra "no existe" cuando la API responde 404', async () => {
    const { harness } = await abrir('/productos/99');
    http.expectOne('/api/productos/99').flush(
      { status: 404, code: 'PRODUCTO_NO_ENCONTRADO', message: 'No existe el producto con id 99.', details: [] },
      { status: 404, statusText: 'Not Found' },
    );

    const contenido = texto(harness);
    expect(contenido).toContain('El producto solicitado no existe');
    expect(contenido).not.toContain('Reintentar');
  });

  it('muestra el error y permite reintentar cuando no hay conexión', async () => {
    const { harness, componente } = await abrir('/productos/1');
    http.expectOne('/api/productos/1').error(new ProgressEvent('error'), { status: 0 });

    expect(texto(harness)).toContain('No se pudo conectar con el servidor');
    expect(texto(harness)).toContain('Reintentar');

    componente.cargar();
    http.expectOne('/api/productos/1').flush({ id: 1, codigo: 'P-001', nombre: 'Laptop', precio: 15000, existencia: 8 });
    expect(texto(harness)).toContain('Laptop');
  });

  it('no consulta la API si el identificador no es válido', async () => {
    const { harness } = await abrir('/productos/abc');

    http.expectNone('/api/productos/abc');
    expect(texto(harness)).toContain('El producto solicitado no existe');
  });
});
