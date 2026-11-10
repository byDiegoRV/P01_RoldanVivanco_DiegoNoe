import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';

import { ErrorApi, Producto } from '../modelos/api.modelos';
import { ProductosService } from '../servicios/productos.service';

export type EstadoCatalogo = 'cargando' | 'listo' | 'vacio' | 'error';

@Component({
  selector: 'app-catalogo',
  imports: [CurrencyPipe],
  templateUrl: './catalogo.component.html',
  styleUrl: './catalogo.component.css',
})
export class CatalogoComponent implements OnInit {
  private readonly productos = inject(ProductosService);

  readonly estado = signal<EstadoCatalogo>('cargando');
  readonly lista = signal<Producto[]>([]);
  readonly error = signal<ErrorApi | null>(null);

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.estado.set('cargando');
    this.error.set(null);
    this.productos.listar().subscribe({
      next: (productos) => {
        this.lista.set(productos);
        this.estado.set(productos.length === 0 ? 'vacio' : 'listo');
      },
      error: (error: ErrorApi) => {
        this.error.set(error);
        this.estado.set('error');
      },
    });
  }
}
