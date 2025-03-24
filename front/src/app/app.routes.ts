import { Routes } from '@angular/router';
import { ListaArticulosComponent } from './landing-page/lista-articulos.component';
import { LoginComponent } from './login/login.component';
import { CarritoComponent } from './carrito/carrito.component';
import { StockComponent } from './stock/stock.component';

export const routes: Routes = [

    { path: "listado", component: ListaArticulosComponent },
    { path: "listaArticulos", component: ListaArticulosComponent },
    { path: "carrito", component: CarritoComponent },
    { path: "listaArticulos", component: ListaArticulosComponent },
    { path: "stock", component: StockComponent },
    { path: "", component: LoginComponent },

];
