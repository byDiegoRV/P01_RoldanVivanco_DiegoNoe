import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';

import { Producto } from '../modelos/api.modelos';
import { aErrorApi } from './errores-api';

@Injectable({ providedIn: 'root' })
export class ProductosService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/productos';

  listar(): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.base).pipe(
      catchError((error) => throwError(() => aErrorApi(error))),
    );
  }

  obtener(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.base}/${id}`).pipe(
      catchError((error) => throwError(() => aErrorApi(error))),
    );
  }
}
