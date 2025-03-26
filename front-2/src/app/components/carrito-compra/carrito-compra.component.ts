import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CarritoCompraService, ItemCarritoCompra } from '../../services/carrito-compra.service';

@Component({
  selector: 'app-carrito-compra',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './carrito-compra.component.html',
  styleUrls: ['./carrito-compra.component.css']
})
export class CarritoCompraComponent implements OnInit {
  items: ItemCarritoCompra[] = [];
  total: number = 0;
  loading: boolean = false;
  error: string = '';
  compraRealizada: boolean = false;

  constructor(
    private carritoCompraService: CarritoCompraService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carritoCompraService.getItems().subscribe(items => {
      this.items = items;
      this.total = this.carritoCompraService.getTotal();
    });
  }

  actualizarCantidad(item: ItemCarritoCompra, evento: any): void {
    const cantidad = parseInt(evento.target.value);
    if (cantidad > 0) {
      this.carritoCompraService.actualizarCantidad(item.producto.id, cantidad);
    }
  }

  actualizarPrecioUnitario(item: ItemCarritoCompra, evento: any): void {
    const precio = parseFloat(evento.target.value);
    if (precio > 0) {
      this.carritoCompraService.actualizarPrecioUnitario(item.producto.id, precio);
    }
  }

  eliminarProducto(productoId: number): void {
    this.carritoCompraService.eliminarProducto(productoId);
  }

  vaciarCarrito(): void {
    this.carritoCompraService.vaciarCarrito();
  }

  finalizarCompra(): void {
    this.loading = true;
    this.error = '';
    this.compraRealizada = false;

    this.carritoCompraService.finalizarCompra().subscribe({
      next: (respuesta) => {
        this.loading = false;
        this.compraRealizada = true;
        this.carritoCompraService.vaciarCarrito();
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.mensaje || 'Error al procesar la compra';
        console.error('Error al finalizar compra:', err);
      }
    });
  }

  continuarComprando(): void {
    this.router.navigate(['/compras/productos']);
  }

  volverAlDashboard(): void {
    this.router.navigate(['/dashboard']);
  }
}
