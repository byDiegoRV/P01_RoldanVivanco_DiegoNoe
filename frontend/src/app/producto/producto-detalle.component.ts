import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { ErrorApi, Producto } from '../modelos/api.modelos';
import { ProductosService } from '../servicios/productos.service';

type EstadoDetalle = 'cargando' | 'listo' | 'no-encontrado' | 'error';

/** Detalle de un producto: consume GET /api/productos/{id} con estados de carga, éxito, no encontrado (404) y error. */
@Component({
  selector: 'app-producto-detalle',
  imports: [CurrencyPipe, RouterLink],
  templateUrl: './producto-detalle.component.html',
  styleUrl: './producto-detalle.component.css',
})
export class ProductoDetalleComponent implements OnInit {
  private readonly productos = inject(ProductosService);
  private readonly ruta = inject(ActivatedRoute);

  readonly estado = signal<EstadoDetalle>('cargando');
  readonly producto = signal<Producto | null>(null);
  readonly error = signal<ErrorApi | null>(null);

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    const id = Number(this.ruta.snapshot.paramMap.get('id'));
    this.producto.set(null);
    this.error.set(null);

    if (!Number.isInteger(id) || id < 1) {
      // Identificador que no puede existir: no se consulta a la API.
      this.estado.set('no-encontrado');
      return;
    }

    this.estado.set('cargando');
    this.productos.obtener(id).subscribe({
      next: (producto) => {
        this.producto.set(producto);
        this.estado.set('listo');
      },
      error: (error: ErrorApi) => {
        if (error.status === 404) {
          this.estado.set('no-encontrado');
        } else {
          this.error.set(error);
          this.estado.set('error');
        }
      },
    });
  }
}
