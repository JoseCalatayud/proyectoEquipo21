import { Injectable } from '@angular/core';
import { Articulo } from './articulo';

@Injectable({
  providedIn: 'root'
})
export class CarritoService {

  articulos: Articulo[] = [];
  constructor() { 

    this.addCarrito(new Articulo(1,"gfdsg","dadafds","dfgsdfg","cdfag",1,"fdsg",2,true));
  }


  addCarrito(articulo: Articulo) {

    this.articulos.push(articulo);
    console.log(this.articulos);
  }

  remove(articulo:Articulo) {
    this.articulos.splice(this.articulos.indexOf(articulo), 1);
  }


}
