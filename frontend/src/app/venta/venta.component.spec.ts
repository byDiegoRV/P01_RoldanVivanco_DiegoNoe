import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { ErrorApi, Producto, VentaRespuesta } from '../modelos/api.modelos';
import { ProductosService } from '../servicios/productos.service';
import { VentasService } from '../servicios/ventas.service';
import { VentaComponent } from './venta.component';

const PRODUCTOS: Producto[] = [
  { id: 1, codigo: 'P-001', nombre: 'Laptop', precio: 15000, existencia: 8 },
  { id: 2, codigo: 'P-002', nombre: 'Mouse', precio: 250, existencia: 4 },
];

const RECIBO: VentaRespuesta = {
  folio: 'V-0001',
  total: 15000,
  partidas: [
    { productoId: 1, nombre: 'Laptop', cantidad: 1, precioUnitario: 15000, subtotal: 15000 },
  ],
};

const ERROR_EXISTENCIA: ErrorApi = {
  status: 409,
  code: 'EXISTENCIA_INSUFICIENTE',
  message: 'Existencia insuficiente para Laptop',
  details: [],
};

// Datos ficticios de prueba: ninguna tarjeta real.
const TITULAR_PRUEBA = 'Cliente Prueba';
const TARJETA_PRUEBA = '4111 1111 1111 1111';

describe('VentaComponent', () => {
  let fixture: ComponentFixture<VentaComponent>;
  let productos: jasmine.SpyObj<ProductosService>;
  let ventas: jasmine.SpyObj<VentasService>;

  const raiz = (): HTMLElement => fixture.nativeElement as HTMLElement;

  function escribir(selector: string, valor: string): void {
    const entrada = raiz().querySelector<HTMLInputElement>(selector)!;
    entrada.value = valor;
    entrada.dispatchEvent(new Event('input'));
  }

  function elegir(id: string, cantidad: number): void {
    const select = raiz().querySelector<HTMLSelectElement>('#producto')!;
    select.value = id;
    select.dispatchEvent(new Event('change'));
    escribir('#cantidad', String(cantidad));
    fixture.detectChanges();
  }

  function agregar(): void {
    raiz()
      .querySelector<HTMLFormElement>('form.agregar')!
      .dispatchEvent(new Event('submit', { cancelable: true }));
    fixture.detectChanges();
  }

  function pagar(titular: string, tarjeta: string): void {
    escribir('#titular', titular);
    escribir('#tarjeta', tarjeta);
    fixture.detectChanges();
  }

  function confirmar(): void {
    raiz().querySelector<HTMLButtonElement>('.confirmar')!.click();
    fixture.detectChanges();
  }

  beforeEach(async () => {
    productos = jasmine.createSpyObj<ProductosService>('ProductosService', ['listar']);
    ventas = jasmine.createSpyObj<VentasService>('VentasService', ['registrar']);
    productos.listar.and.returnValue(of(PRODUCTOS));

    await TestBed.configureTestingModule({
      imports: [VentaComponent],
      providers: [
        { provide: ProductosService, useValue: productos },
        { provide: VentasService, useValue: ventas },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(VentaComponent);
    fixture.detectChanges();
  });

  it('muestra los productos en el selector', () => {
    expect(raiz().querySelectorAll('#producto option').length).toBe(3);
  });

  it('agrega un producto al carrito y calcula el total', () => {
    elegir('1', 2);
    agregar();

    expect(raiz().querySelectorAll('.carrito tbody tr').length).toBe(1);
    expect(raiz().querySelector('.total')?.textContent).toContain('30,000.00');
  });

  it('rechaza una cantidad mayor a la existencia', () => {
    elegir('2', 5);
    agregar();

    expect(raiz().querySelector('.aviso')?.textContent).toContain('Solo hay 4');
    expect(raiz().querySelector('.carrito tbody tr')).toBeNull();
  });

  it('no permite confirmar con el carrito vacío', () => {
    confirmar();

    expect(raiz().querySelector('.aviso')?.textContent).toContain('Agrega al menos un producto');
    expect(ventas.registrar).not.toHaveBeenCalled();
  });

  it('rechaza una tarjeta que no tiene 16 dígitos', () => {
    elegir('1', 1);
    agregar();
    pagar(TITULAR_PRUEBA, '1234');
    confirmar();

    expect(raiz().querySelector('.error-pago')?.textContent).toContain('16 dígitos');
    expect(ventas.registrar).not.toHaveBeenCalled();
    expect(raiz().querySelectorAll('.carrito tbody tr').length).toBe(1);
  });

  it('exige el nombre del titular', () => {
    elegir('1', 1);
    agregar();
    pagar('', TARJETA_PRUEBA);
    confirmar();

    expect(raiz().querySelector('.error-pago')?.textContent).toContain('nombre del titular');
    expect(ventas.registrar).not.toHaveBeenCalled();
  });

  it('registra la venta sin enviar el pago, muestra el folio y borra los datos de pago', () => {
    ventas.registrar.and.returnValue(of(RECIBO));
    elegir('1', 1);
    agregar();
    pagar(TITULAR_PRUEBA, TARJETA_PRUEBA);
    confirmar();

    expect(ventas.registrar).toHaveBeenCalledWith({
      partidas: [{ productoId: 1, cantidad: 1 }],
    });
    const recibo = raiz().querySelector('.recibo')?.textContent;
    expect(recibo).toContain('V-0001');
    expect(recibo).toContain('1111');
    expect(recibo).not.toContain('4111');
    expect(raiz().querySelector('.carrito tbody tr')).toBeNull();
    expect(raiz().querySelector<HTMLInputElement>('#tarjeta')!.value).toBe('');
    expect(raiz().querySelector<HTMLInputElement>('#titular')!.value).toBe('');
    expect(productos.listar).toHaveBeenCalledTimes(2);
  });

  it('muestra el mensaje de la API cuando la venta falla y conserva el carrito', () => {
    ventas.registrar.and.returnValue(throwError(() => ERROR_EXISTENCIA));
    elegir('1', 1);
    agregar();
    pagar(TITULAR_PRUEBA, TARJETA_PRUEBA);
    confirmar();

    expect(raiz().querySelector('.error-venta')?.textContent).toContain(
      'Existencia insuficiente para Laptop',
    );
    expect(raiz().querySelector('.recibo')).toBeNull();
    expect(raiz().querySelectorAll('.carrito tbody tr').length).toBe(1);
  });
});
