import { Injectable } from '@angular/core';
import { Carrito } from './carrito';
import { Articulo } from './articulo';

@Injectable({
  providedIn: 'root'
})
export class CarritoService {
  listaCarrito: Carrito[] = [];

  constructor() { }

  addCarrito(articulo: Articulo) {
    let item = new Carrito();
    item.id = articulo.id;
    item.nombre = articulo.nombre;
    item.codigoBarras = articulo.codigoBarras;
    item.precio = articulo.precioVenta;
    item.cantidad = 1;
    this.listaCarrito.push(item);
  }
}
