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
  styleUrl: './carrito.component.scss',
  standalone: true // Agregado: standalone: true
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
    this.articuloRestService.crearVenta(this.listaCarrito).subscribe({
      next: (datos) => {
        console.log('Venta creada correctamente:', datos);
        alert("Pago efectuado y stock actualizado");
        this.vaciarCarrito();
        // Aquí puedes agregar lógica adicional si la actualización fue exitosa
      },
      error: (error) => {
        console.error('Error al crear la venta:', error);
        alert("Error al crear la venta");
        // Aquí puedes agregar lógica para manejar el error
      }
    });
  }

}

