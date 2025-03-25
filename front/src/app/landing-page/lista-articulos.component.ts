import { Component, OnInit } from '@angular/core';
import { ArticuloRestService } from '../articulo-rest.service';
import { Articulo } from '../articulo';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgFor } from '@angular/common';
import { CarritoService } from '../carrito.service';

@Component({
  selector: 'app-lista-articulos',
  imports: [RouterLink, FormsModule, NgFor, CommonModule],
  templateUrl: './lista-articulos.component.html',
  styleUrl: './lista-articulos.component.scss',
  standalone: true // Agregado: standalone: true
})
export class ListaArticulosComponent implements OnInit {
  listaArticulos: Articulo[] = [];

  constructor(private articuloRestService: ArticuloRestService, private carritoService: CarritoService) { }

  ngOnInit(): void {
    this.cargarArticulos();
  }

  cargarArticulos() {
    this.articuloRestService.buscarTodos().subscribe(data => {
      this.listaArticulos = data;
    });
  }

  agregarCarrito(articulo: Articulo) {
    this.carritoService.addCarrito(articulo);
  }

  verDetalle(id: number) {
    console.log("Ver detalle del artículo con ID:", id);
  }
}
