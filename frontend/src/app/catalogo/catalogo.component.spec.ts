import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CatalogoComponent } from './catalogo.component';

describe('CatalogoComponent', () => {
  let fixture: ComponentFixture<CatalogoComponent>;
  let http: HttpTestingController;

  const texto = (): string => (fixture.nativeElement as HTMLElement).textContent ?? '';

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CatalogoComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    fixture = TestBed.createComponent(CatalogoComponent);
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => http.verify());

  it('muestra el estado de carga mientras responde la API', () => {
    expect(texto()).toContain('Cargando productos');
    http.expectOne('/api/productos').flush([]);
  });

  it('muestra la lista de productos cuando la API responde', () => {
    http.expectOne('/api/productos').flush([
      { id: 1, codigo: 'P-001', nombre: 'Laptop', precio: 15000, existencia: 8 },
      { id: 2, codigo: 'P-002', nombre: 'Mouse', precio: 250, existencia: 0 },
    ]);
    fixture.detectChanges();

    const filas = (fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr');
    expect(filas.length).toBe(2);
    expect(texto()).toContain('Laptop');
    expect(texto()).toContain('Agotado');
    expect(texto()).not.toContain('Cargando productos');
  });

  it('muestra un mensaje cuando no hay productos', () => {
    http.expectOne('/api/productos').flush([]);
    fixture.detectChanges();

    expect(texto()).toContain('No hay productos disponibles');
  });

  it('muestra el mensaje de la API cuando falla y permite reintentar', () => {
    http.expectOne('/api/productos').flush(
      { status: 500, code: 'ERROR_INTERNO', message: 'Ocurrió un error interno. Intenta de nuevo más tarde.', details: [] },
      { status: 500, statusText: 'Internal Server Error' },
    );
    fixture.detectChanges();

    expect(texto()).toContain('Ocurrió un error interno');
    expect((fixture.nativeElement as HTMLElement).querySelector('button')).not.toBeNull();

    fixture.componentInstance.cargar();
    http.expectOne('/api/productos').flush([]);
  });

  it('indica falta de conexión cuando la API no responde', () => {
    http.expectOne('/api/productos').error(new ProgressEvent('error'));
    fixture.detectChanges();

    expect(texto()).toContain('No se pudo conectar con el servidor');
  });
});
