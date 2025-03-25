import { Routes } from '@angular/router';
import { ListaArticulosComponent } from './landing-page/lista-articulos.component';
import { LoginComponent } from './login/login.component';
import { CarritoComponent } from './carrito/carrito.component';
import { StockComponent } from './stock/stock.component';
import { DetalleComponent } from './detalle/detalle.component';
import { HistoricoVentasComponent } from './historico-ventas/historico-ventas.component';

export const routes: Routes = [

    { path: "listado", component: ListaArticulosComponent },
    { path: "listaArticulos", component: ListaArticulosComponent },
    { path: "carrito", component: CarritoComponent },
    { path: "detalle/:id", component: DetalleComponent },
    { path: "listaArticulos", component: ListaArticulosComponent },
    { path: "stock", component: StockComponent },
    { path: "detalleVenta", component: HistoricoVentasComponent },
    { path: "", component: LoginComponent },

];
