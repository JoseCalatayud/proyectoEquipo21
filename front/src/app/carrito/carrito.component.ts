import { Component, OnInit } from '@angular/core';
import { ArticuloRestService } from '../articulo-rest.service';
import { Articulo } from '../articulo';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgFor } from '@angular/common';
import { CarritoService } from '../carrito.service';
import { Carrito } from '../carrito';


@Component({
  selector: 'app-carrito',
  imports: [RouterLink, FormsModule, NgFor, CommonModule],
  templateUrl: './carrito.component.html',
  styleUrl: './carrito.component.scss'
})
export class CarritoComponent implements OnInit {
  listaCarrito: Carrito[] = [];
  subtotal: number = 0;

  constructor(private articuloRestService: ArticuloRestService, private carritoService: CarritoService) {


    this.listaCarrito = carritoService.listaCarrito;
  }

  ngOnInit(): void {
    this.calcularSubtotal();
  }

  calcularSubtotal() {
    this.subtotal = 0; // Reset subtotal before recalculating
    for (const item of this.listaCarrito) {
      this.subtotal += item.precio * item.cantidad;
    }
  }
  vaciarCarrito() {
    this.carritoService.listaCarrito = [];
    this.listaCarrito = [];
    this.calcularSubtotal();

  }
  pagar() {
    alert("Pago efectuado");
   
    this.articuloRestService.actualizarStock(this.listaCarrito).subscribe((datos=> {
      console.log(datos);
      this.vaciarCarrito();
    }));
  }

}

