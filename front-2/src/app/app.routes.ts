import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ProductosMenuComponent } from './components/productos-menu/productos-menu.component';
import { ProductosListaComponent } from './components/productos-lista/productos-lista.component';
import { ProductoCrearComponent } from './components/producto-crear/producto-crear.component';
import { ProductoEditarComponent } from './components/producto-editar/producto-editar.component';
import { ProductoDetalleComponent } from './components/producto-detalle/producto-detalle.component';
import { CarritoComponent } from './components/carrito/carrito.component';
import { VentasMenuComponent } from './components/ventas-menu/ventas-menu.component';
import { VentasHistorialComponent } from './components/ventas-historial/ventas-historial.component';
import { ComprasMenuComponent } from './components/compras-menu/compras-menu.component';
import { ComprasHistorialComponent } from './components/compras-historial/compras-historial.component';
import { CompraCrearComponent } from './components/compra-crear/compra-crear.component';
import { ComprasProductosComponent } from './components/compras-productos/compras-productos.component';
import { CarritoCompraComponent } from './components/carrito-compra/carrito-compra.component';
import { AuthGuard } from './guards/auth-guard.guard';
import { AdminGuard } from './guards/admin.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },

  // Rutas para gestión de productos
  {
    path: 'productos',
    component: ProductosMenuComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'productos/listar',
    component: ProductosListaComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'productos/detalle/:id',
    component: ProductoDetalleComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'productos/crear',
    component: ProductoCrearComponent,
    canActivate: [AuthGuard, AdminGuard]
  },
  {
    path: 'productos/editar/:id',
    component: ProductoEditarComponent,
    canActivate: [AuthGuard, AdminGuard]
  },

  // Rutas para ventas
  {
    path: 'ventas',
    component: VentasMenuComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'ventas/productos',
    component: ProductosListaComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'ventas/carrito',
    component: CarritoComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'ventas/historial',
    component: VentasHistorialComponent,
    canActivate: [AuthGuard]
  },

  // Rutas para compras (solo admin)
  {
    path: 'compras',
    component: ComprasMenuComponent,
    canActivate: [AuthGuard, AdminGuard]
  },
  {
    path: 'compras/productos',
    component: ComprasProductosComponent,
    canActivate: [AuthGuard, AdminGuard]
  },
  {
    path: 'compras/carrito',
    component: CarritoCompraComponent,
    canActivate: [AuthGuard, AdminGuard]
  },
  {
    path: 'compras/historial',
    component: ComprasHistorialComponent,
    canActivate: [AuthGuard, AdminGuard]
  },
  {
    path: 'compras/crear',
    component: CompraCrearComponent,
    canActivate: [AuthGuard, AdminGuard]
  },

  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
