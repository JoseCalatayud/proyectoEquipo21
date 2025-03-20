import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ListaArticulosComponent } from "./lista-articulos/lista-articulos.component";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ListaArticulosComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'grupoVentas';
}
