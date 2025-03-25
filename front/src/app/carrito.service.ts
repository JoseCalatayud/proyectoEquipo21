import { Injectable } from '@angular/core';
import { Articulo } from './articulo';
import { Carrito } from './carrito';

@Injectable({
  providedIn: 'root'
})
export class CarritoService {

  listaCarrito: Carrito[] = [];


  constructor() {

  }

  addCarrito(articulo: Articulo) {

    let carritoEncontrado:Carrito| undefined=this.listaCarrito.find(c => c.nombre == articulo.nombre)

    if (carritoEncontrado) {
      carritoEncontrado.cantidad++;
     
    }else {
      let c: Carrito = new Carrito(articulo.codigoBarras,articulo.nombre, 1, articulo.precioVenta);
      this.listaCarrito.push(c);
    }

  }

}
