import { Component, OnInit } from '@angular/core';
import { ArticuloRestService } from '../articulo-rest.service';
import { Articulo } from '../articulo';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgFor } from '@angular/common';

@Component({
  selector: 'app-stock',
  imports: [RouterLink, FormsModule, NgFor, CommonModule],
  templateUrl: './stock.component.html',
  styleUrl: './stock.component.scss'
})
export class StockComponent {

  listaArticulos: Articulo[] = [];
  articulo: Articulo = {} as Articulo;
  constructor(private articuloRestService: ArticuloRestService) {
    this.articuloRestService.buscarTodos().subscribe((datos) => {
      this.listaArticulos = datos;
    })

    console.log("hola");
    const codificado = btoa("admin:admin123");

    console.log("Codificado:", codificado);
  }

  borrar(articulo: Articulo) {

    this.articuloRestService.borrar(articulo).subscribe((datos) => {
      this.articuloRestService.buscarTodos().subscribe((datos) => {
        this.listaArticulos = datos;
      })
    })
  }

  editar(articulo: Articulo) {
    this.articuloRestService.editar(articulo).subscribe((datos) => {
      this.articuloRestService.buscarTodos().subscribe((datos) => {
        this.listaArticulos = datos;
      })
    })
  }

  insertar(articulo: Articulo) {
    console.log("inserta");
    this.articuloRestService.insertar(articulo).subscribe((datos) => {
      this.articuloRestService.buscarTodos().subscribe((datos) => {
        this.listaArticulos = datos;
      })
    })
  }

  agregarCarrito(id: number) {
    this.articuloRestService.agregarCarrito(id).subscribe((datos) => {
      this.listaArticulos = datos;
    })

  }

}
