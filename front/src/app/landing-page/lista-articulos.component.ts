import { Component, OnInit } from '@angular/core';
import { ArticuloRestService } from '../articulo-rest.service';
import { Articulo } from '../articulo';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgFor } from '@angular/common';
import { CarritoService } from '../carrito.service';

@Component({
  selector: 'app-lista-articulos',
  imports: [RouterLink, FormsModule, NgFor, CommonModule],
  templateUrl: './lista-articulos.component.html',
  styleUrl: './lista-articulos.component.scss'
})
export class ListaArticulosComponent {
  listaArticulos: Articulo[] = [];

  
  constructor(private articuloRestService: ArticuloRestService,private router:Router,private carritoService:CarritoService) {
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

  agregarCarrito(articulo: Articulo) {

    this.carritoService.addCarrito(articulo);
    /*
    this.articuloRestService.agregarCarrito(id).subscribe((datos) => {
      this.listaArticulos = datos;
    })
*/
  }

  verDetalle(id: number) {

    this.router.navigate(["/detalle",id]);
    /*
    this.articuloRestService.verDetalle(id).subscribe((datos) => {
      this.listaArticulos = datos;
    })
    */

  }

}



