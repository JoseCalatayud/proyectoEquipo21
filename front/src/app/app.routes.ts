import { Routes } from '@angular/router';
import { ListaArticulosComponent } from './landing-page/lista-articulos.component';
import { LoginComponent } from './login/login.component';
import { CarritoComponent } from './carrito/carrito.component';
import { StockComponent } from './stock/stock.component';
import { DetalleComponent } from './detalle/detalle.component';

export const routes: Routes = [

    { path: "listado", component: ListaArticulosComponent },
    { path: "listaArticulos", component: ListaArticulosComponent },
    { path: "carrito", component: CarritoComponent },
    { path: "detalle/:id", component: DetalleComponent },
    { path: "listaArticulos", component: ListaArticulosComponent },
    { path: "stock", component: StockComponent },
    { path: "", component: LoginComponent },

];
