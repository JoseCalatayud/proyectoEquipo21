import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  codigoBarras: string;
  familia: string;
  fotografia: string;
  precioVenta: number;
  stock: number;
  precioPromedioPonderado: number;
  borrado: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ProductoService {

  constructor(private http: HttpClient) {}

  listarProductos(): Observable<Producto[]> {
    return this.http.get<Producto[]>('/api/articulos');
  }

  obtenerProductoPorId(id: number): Observable<Producto> {
    return this.http.get<Producto>(`/api/articulos/${id}`);
  }

  buscarProductos(filtro: string): Observable<Producto[]> {
    return this.http.get<Producto[]>(`/api/articulos/buscar?nombre=${filtro}`);
  }

  // Método para actualizar un producto
  actualizarProducto(id: number, producto: Partial<Producto>): Observable<Producto> {
    return this.http.put<Producto>(`/api/articulos/actualizar/${id}`, producto);
  }

  // Método para crear un producto
  crearProducto(producto: Partial<Producto>): Observable<Producto> {
    return this.http.post<Producto>('/api/articulos', producto);
  }

  // Métodos para activar/desactivar productos
  desactivarProducto(id: number): Observable<any> {
    return this.http.post<any>(`/api/articulos/borrar/${id}`, {});
  }

  activarProducto(id: number): Observable<any> {
    return this.http.post<any>(`/api/articulos/activar/${id}`, {});
  }
}
