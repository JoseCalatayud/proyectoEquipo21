import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Producto } from './producto.service';
import { HttpClient } from '@angular/common/http';

export interface ItemCarritoCompra {
  producto: Producto;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface CompraRequest {
  detalles: {
    idArticulo: number;
    cantidad: number;
    precioUnitario: number;
  }[];
}

@Injectable({
  providedIn: 'root'
})
export class CarritoCompraService {
  private items: ItemCarritoCompra[] = [];
  private itemsSubject = new BehaviorSubject<ItemCarritoCompra[]>([]);

  constructor(private http: HttpClient) {}

  getItems(): Observable<ItemCarritoCompra[]> {
    return this.itemsSubject.asObservable();
  }

  agregarProducto(producto: Producto, cantidad: number, precioUnitario: number): void {
    // Verificar si el producto ya está en el carrito
    const itemExistente = this.items.find(item => item.producto.id === producto.id);

    if (itemExistente) {
      itemExistente.cantidad += cantidad;
      itemExistente.precioUnitario = precioUnitario;
      itemExistente.subtotal = itemExistente.cantidad * precioUnitario;
    } else {
      this.items.push({
        producto,
        cantidad,
        precioUnitario,
        subtotal: cantidad * precioUnitario
      });
    }

    this.itemsSubject.next([...this.items]);
  }

  actualizarCantidad(productoId: number, cantidad: number): void {
    const item = this.items.find(item => item.producto.id === productoId);

    if (item) {
      item.cantidad = cantidad;
      item.subtotal = cantidad * item.precioUnitario;
      this.itemsSubject.next([...this.items]);
    }
  }

  actualizarPrecioUnitario(productoId: number, precioUnitario: number): void {
    const item = this.items.find(item => item.producto.id === productoId);

    if (item) {
      item.precioUnitario = precioUnitario;
      item.subtotal = item.cantidad * precioUnitario;
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

  finalizarCompra(): Observable<any> {
    const compra: CompraRequest = {
      detalles: this.items.map(item => ({
        idArticulo: item.producto.id,
        cantidad: item.cantidad,
        precioUnitario: item.precioUnitario
      }))
    };

    return this.http.post('/api/compras', compra);
  }
}
