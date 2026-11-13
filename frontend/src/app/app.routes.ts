import { Routes } from '@angular/router';

import { CatalogoComponent } from './catalogo/catalogo.component';
import { VentaComponent } from './venta/venta.component';

export const routes: Routes = [
  { path: '', component: CatalogoComponent },
  { path: 'venta', component: VentaComponent },
  { path: '**', redirectTo: '' },
];
