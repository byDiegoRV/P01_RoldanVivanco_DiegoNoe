import { Routes } from '@angular/router';

import { CatalogoComponent } from './catalogo/catalogo.component';
import { ProductoDetalleComponent } from './producto/producto-detalle.component';
import { VentaComponent } from './venta/venta.component';

export const routes: Routes = [
  { path: '', component: CatalogoComponent },
  { path: 'venta', component: VentaComponent },
  { path: 'productos/:id', component: ProductoDetalleComponent },
  { path: '**', redirectTo: '' },
];
