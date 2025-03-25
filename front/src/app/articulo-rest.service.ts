import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Articulo } from './articulo';
import { setThrowInvalidWriteToSignalError } from '@angular/core/primitives/signals';
import { Carrito } from './carrito';
import { ArticuloUpdateRequest } from './articulo-update-request';

//testdb nombre de la BBDD

@Injectable({
  providedIn: 'root'
})
export class ArticuloRestService {

  constructor(private httpClient: HttpClient) { }

  crearVenta(listaCarrito: Carrito[]): Observable<any> {
    // Transformar los datos del carrito al formato que espera el backend
    const datosParaBackend = {
      detalles: listaCarrito.map(item => ({
        codigoBarras: item.codigoBarras,
        cantidad: item.cantidad
      }))
    };

    const headers = new HttpHeaders({
      'Authorization': 'Basic ' + btoa('admin:admin123') // Reemplaza con tu lógica de autenticación
    });

    // Enviar la solicitud POST al backend
    return this.httpClient.post<any>('/api/ventas/crear', datosParaBackend, { headers });
  }
  

  public buscarTodos(): Observable<Articulo[]> {

    return this.httpClient.get<Articulo[]>("/api/articulos"); // Corregido
  }

  public borrar(articulo: Articulo): Observable<Articulo> {
    return this.httpClient.post<Articulo>(`/api/articulos/borrar/${articulo.id}`, articulo) // Corregido

  }

  public activar(articulo: Articulo): Observable<Articulo> {
    return this.httpClient.post<Articulo>(`/api/articulos/activar/${articulo.id}`, articulo) // Corregido

  }

  public insertar(articulo: Articulo): Observable<Articulo> {

    return this.httpClient.post<Articulo>("/api/articulos", articulo) // Corregido

  }

  public editar(id: number, articuloUpdateRequest: ArticuloUpdateRequest): Observable<Articulo> { // Modificado
    const headers = new HttpHeaders({
      'Content-Type': 'application/json' // Añadido
    });
    return this.httpClient.put<Articulo>(`/api/articulos/${id}`, articuloUpdateRequest, { headers }); // Modificado y Corregido
  }

  public agregarCarrito(id: number): Observable<Articulo[]> {

    return this.httpClient.post<Articulo[]>("/api/articulos/", id) // Corregido

  }

  public verDetalle(id: number): Observable<Articulo> {
    return this.httpClient.get<Articulo>(`/api/articulos/${id}`) // Corregido
  }
  
  actualizarStock(listaCarrito: Carrito[]): Observable<any> {
    // Transformar los datos del carrito al formato que espera el backend
    const datosParaBackend = listaCarrito.map(item => ({
      idArticulo: item.id, // Asegúrate de que el backend espera el ID del artículo
      cantidadVendida: item.cantidad
    }));

    const headers = new HttpHeaders({
      'Authorization': 'Basic ' + btoa('admin:admin123') // Reemplaza con tu lógica de autenticación
    });

    // Enviar la solicitud PUT al backend
    return this.httpClient.put<any>('/api/articulos/actualizarStock', datosParaBackend, { headers }); // Reemplaza '/api/articulos/actualizarStock' con el endpoint correcto
  }


}
