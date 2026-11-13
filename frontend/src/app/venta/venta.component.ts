import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';

import { ErrorApi, Producto, VentaRespuesta } from '../modelos/api.modelos';
import { ProductosService } from '../servicios/productos.service';
import { VentasService } from '../servicios/ventas.service';

export interface LineaCarrito {
  producto: Producto;
  cantidad: number;
}

@Component({
  selector: 'app-venta',
  imports: [CurrencyPipe],
  templateUrl: './venta.component.html',
  styleUrl: './venta.component.css',
})
export class VentaComponent implements OnInit {
  private readonly productosService = inject(ProductosService);
  private readonly ventasService = inject(VentasService);

  readonly productos = signal<Producto[]>([]);
  readonly errorCarga = signal<ErrorApi | null>(null);
  readonly carrito = signal<LineaCarrito[]>([]);
  readonly productoId = signal<number | null>(null);
  readonly cantidad = signal(1);
  readonly aviso = signal<string | null>(null);
  readonly enviando = signal(false);
  readonly recibo = signal<VentaRespuesta | null>(null);
  readonly errorVenta = signal<ErrorApi | null>(null);

  // Pago simulado: estos datos solo viven en el cliente. No se envian a la API,
  // no se guardan y se borran al confirmar la venta.
  readonly titular = signal('');
  readonly tarjeta = signal('');
  readonly errorPago = signal<string | null>(null);
  readonly terminacion = signal<string | null>(null);

  readonly total = computed(() =>
    this.carrito().reduce((suma, linea) => suma + linea.producto.precio * linea.cantidad, 0),
  );

  ngOnInit(): void {
    this.cargarProductos();
  }

  cargarProductos(): void {
    this.errorCarga.set(null);
    this.productosService.listar().subscribe({
      next: (lista) => this.productos.set(lista),
      error: (e: ErrorApi) => this.errorCarga.set(e),
    });
  }

  seleccionar(valor: string): void {
    this.productoId.set(valor === '' ? null : Number(valor));
    this.aviso.set(null);
  }

  cambiarCantidad(valor: number): void {
    this.cantidad.set(valor);
  }

  cambiarTitular(valor: string): void {
    this.titular.set(valor);
    this.errorPago.set(null);
  }

  cambiarTarjeta(valor: string): void {
    this.tarjeta.set(valor);
    this.errorPago.set(null);
  }

  agregar(): void {
    const producto = this.productos().find((p) => p.id === this.productoId());
    if (!producto) {
      this.aviso.set('Selecciona un producto.');
      return;
    }
    const cantidad = this.cantidad();
    if (!Number.isInteger(cantidad) || cantidad < 1) {
      this.aviso.set('La cantidad debe ser un entero mayor o igual a 1.');
      return;
    }
    const existente = this.carrito().find((l) => l.producto.id === producto.id);
    const nueva = (existente?.cantidad ?? 0) + cantidad;
    if (nueva > producto.existencia) {
      this.aviso.set(`Solo hay ${producto.existencia} pieza(s) de ${producto.nombre}.`);
      return;
    }
    this.carrito.update((lineas) =>
      existente
        ? lineas.map((l) => (l.producto.id === producto.id ? { ...l, cantidad: nueva } : l))
        : [...lineas, { producto, cantidad }],
    );
    this.aviso.set(null);
    this.recibo.set(null);
    this.terminacion.set(null);
    this.errorVenta.set(null);
  }

  quitar(id: number): void {
    this.carrito.update((lineas) => lineas.filter((l) => l.producto.id !== id));
  }

  confirmar(): void {
    if (this.carrito().length === 0) {
      this.aviso.set('Agrega al menos un producto al carrito.');
      return;
    }
    const digitos = this.tarjeta().replace(/\s/g, '');
    if (!this.validarPago(digitos)) {
      return;
    }
    this.aviso.set(null);
    this.errorVenta.set(null);
    this.enviando.set(true);
    // El cuerpo solo lleva las partidas: el pago simulado nunca sale del cliente.
    const peticion = {
      partidas: this.carrito().map((l) => ({ productoId: l.producto.id, cantidad: l.cantidad })),
    };
    this.ventasService.registrar(peticion).subscribe({
      next: (respuesta) => {
        this.recibo.set(respuesta);
        this.terminacion.set(digitos.slice(-4));
        this.carrito.set([]);
        this.titular.set('');
        this.tarjeta.set('');
        this.enviando.set(false);
        this.cargarProductos();
      },
      error: (e: ErrorApi) => {
        this.errorVenta.set(e);
        this.enviando.set(false);
      },
    });
  }

  private validarPago(digitos: string): boolean {
    if (this.titular().trim().length < 3) {
      this.errorPago.set('Escribe el nombre del titular de la tarjeta.');
      return false;
    }
    if (!/^\d{16}$/.test(digitos)) {
      this.errorPago.set('El número de tarjeta debe tener 16 dígitos.');
      return false;
    }
    this.errorPago.set(null);
    return true;
  }
}
