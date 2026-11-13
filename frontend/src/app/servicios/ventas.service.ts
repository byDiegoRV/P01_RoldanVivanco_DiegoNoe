import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';

import { VentaPeticion, VentaRespuesta } from '../modelos/api.modelos';
import { aErrorApi } from './errores-api';

@Injectable({ providedIn: 'root' })
export class VentasService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/ventas';

  registrar(peticion: VentaPeticion): Observable<VentaRespuesta> {
    return this.http.post<VentaRespuesta>(this.base, peticion).pipe(
      catchError((error) => throwError(() => aErrorApi(error))),
    );
  }
}
