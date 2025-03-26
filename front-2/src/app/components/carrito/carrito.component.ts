// src/app/components/carrito/carrito.component.ts
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CarritoService, ItemCarrito } from '../../services/carrito.service';

@Component({
  selector: 'app-carrito',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './carrito.component.html',
  styleUrls: ['./carrito.component.css']
})
export class CarritoComponent implements OnInit {
  items: ItemCarrito[] = [];
  total: number = 0;
  loading: boolean = false;
  error: string = '';
  ventaRealizada: boolean = false;

  constructor(
    private carritoService: CarritoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carritoService.getItems().subscribe(items => {
      this.items = items;
      this.total = this.carritoService.getTotal();
    });
  }

  actualizarCantidad(item: ItemCarrito, evento: any): void {
    const cantidad = parseInt(evento.target.value);
    if (cantidad > 0) {
      this.carritoService.actualizarCantidad(item.producto.id, cantidad);
    }
  }

  eliminarProducto(productoId: number): void {
    this.carritoService.eliminarProducto(productoId);
  }

  vaciarCarrito(): void {
    this.carritoService.vaciarCarrito();
  }

  finalizarVenta(): void {
    this.loading = true;
    this.error = '';
    this.ventaRealizada = false;

    this.carritoService.finalizarVenta().subscribe({
      next: (respuesta) => {
        this.loading = false;
        this.ventaRealizada = true;
        this.carritoService.vaciarCarrito();
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.mensaje || 'Error al procesar la venta';
        console.error('Error al finalizar venta:', err);
      }
    });
  }

  continuarComprando(): void {
    this.router.navigate(['/ventas/productos']);
  }

  volverAlDashboard(): void {
    this.router.navigate(['/dashboard']);
  }
}
