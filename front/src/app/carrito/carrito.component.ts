import { Component, OnInit } from '@angular/core';
import { ArticuloRestService } from '../articulo-rest.service';
import { Articulo } from '../articulo';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgFor } from '@angular/common';


@Component({
  selector: 'app-carrito',
  imports: [RouterLink, FormsModule, NgFor, CommonModule],
  templateUrl: './carrito.component.html',
  styleUrl: './carrito.component.scss'
})
export class CarritoComponent {
  listaArticulos: Articulo[] = [];

  constructor(private articuloRestService: ArticuloRestService) { }

  agregarCarrito(id: number) {
    this.articuloRestService.agregarCarrito(id).subscribe((datos) => {
      this.listaArticulos = datos;
    })
  }
}

