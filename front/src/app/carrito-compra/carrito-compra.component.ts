import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CarritoService } from '../carrito.service';
import { Carrito } from '../carrito';
import { CompraRestService } from '../compra-rest.service';

@Component({
  selector: 'app-carrito-compra',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './carrito-compra.component.html',
  styleUrl: './carrito-compra.component.scss'
})
export class CarritoCompraComponent implements OnInit {
  listaCarrito: Carrito[] = [];
  subtotal: number = 0;

  constructor(private carritoService: CarritoService, private compraRestService: CompraRestService) {
    this.listaCarrito = carritoService.listaCarrito;
  }

  ngOnInit(): void {
    this.calcularSubtotal();
  }

  calcularSubtotal() {
    this.subtotal = 0;
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
    this.compraRestService.crearCompra(this.listaCarrito).subscribe({
      next: (datos) => {
        console.log('Compra creada correctamente:', datos);
        alert("Compra efectuada y stock actualizado");
        this.vaciarCarrito();
      },
      error: (error) => {
        console.error('Error al crear la compra:', error);
        alert("Error al crear la compra");
      }
    });
  }
}
