import { Routes } from '@angular/router';
import { ListaArticulosComponent } from './lista-articulos/lista-articulos.component';
import { LoginComponent } from './login/login.component';

export const routes: Routes = [

    { path: "listado", component: ListaArticulosComponent },
    { path: "stock", component: ListaArticulosComponent },
    { path: "listaArticulos", component: ListaArticulosComponent },
    { path: "", component: LoginComponent },

];
