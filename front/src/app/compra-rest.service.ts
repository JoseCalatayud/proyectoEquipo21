import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Carrito } from './carrito';
import { Compra } from './compra'; // Importamos la clase Compra

@Injectable({
  providedIn: 'root'
})
export class CompraRestService {

  constructor(private httpClient: HttpClient) { }

  crearCompra(listaCarrito: Carrito[]): Observable<any> {
    const datosParaBackend = {
      detalles: listaCarrito.map(item => ({
        idArticulo: item.id,
        cantidad: item.cantidad,
        precio: item.precio
      }))
    };

    const headers = new HttpHeaders({
      'Authorization': 'Basic ' + btoa('admin:admin123') // Reemplaza con tu lógica de autenticación
    });

    return this.httpClient.post<any>('/api/compras/crear', datosParaBackend, { headers });
  }

  listarCompras(): Observable<Compra[]> {
    const headers = new HttpHeaders({
      'Authorization': 'Basic ' + btoa('admin:admin123') // Reemplaza con tu lógica de autenticación
    });
    return this.httpClient.get<Compra[]>('/api/compras/listar', { headers });
  }
}
