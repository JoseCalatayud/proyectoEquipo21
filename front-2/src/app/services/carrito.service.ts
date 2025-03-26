import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Producto } from './producto.service';
import { HttpClient } from '@angular/common/http';

export interface ItemCarrito {
  producto: Producto;
  cantidad: number;
  subtotal: number;
}

export interface VentaRequest {
  detalles: {
    codigoBarras: string;
    cantidad: number;
  }[];
}

@Injectable({
  providedIn: 'root'
})
export class CarritoService {
  private items: ItemCarrito[] = [];
  private itemsSubject = new BehaviorSubject<ItemCarrito[]>([]);

  constructor(private http: HttpClient) {}

  getItems(): Observable<ItemCarrito[]> {
    return this.itemsSubject.asObservable();
  }

  agregarProducto(producto: Producto, cantidad: number): void {
    // Verificar si el producto ya está en el carrito
    const itemExistente = this.items.find(item => item.producto.id === producto.id);

    if (itemExistente) {
      itemExistente.cantidad += cantidad;
      itemExistente.subtotal = itemExistente.cantidad * producto.precioVenta;
    } else {
      this.items.push({
        producto,
        cantidad,
        subtotal: cantidad * producto.precioVenta
      });
    }

    this.itemsSubject.next([...this.items]);
  }

  actualizarCantidad(productoId: number, cantidad: number): void {
    const item = this.items.find(item => item.producto.id === productoId);

    if (item) {
      item.cantidad = cantidad;
      item.subtotal = cantidad * item.producto.precioVenta;
      this.itemsSubject.next([...this.items]);
    }
  }

  eliminarProducto(productoId: number): void {
    this.items = this.items.filter(item => item.producto.id !== productoId);
    this.itemsSubject.next([...this.items]);
  }

  vaciarCarrito(): void {
    this.items = [];
    this.itemsSubject.next([]);
  }

  getTotalItems(): number {
    return this.items.reduce((total, item) => total + item.cantidad, 0);
  }

  getTotal(): number {
    return this.items.reduce((total, item) => total + item.subtotal, 0);
  }

  finalizarVenta(): Observable<any> {
    const venta: VentaRequest = {
      detalles: this.items.map(item => ({
        codigoBarras: item.producto.codigoBarras,
        cantidad: item.cantidad
      }))
    };

    return this.http.post('/api/ventas/crear', venta);
  }
}
