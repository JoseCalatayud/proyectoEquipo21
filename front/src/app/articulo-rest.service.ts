import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Articulo } from './articulo';
import { setThrowInvalidWriteToSignalError } from '@angular/core/primitives/signals';

//testdb nombre de la BBDD

@Injectable({
  providedIn: 'root'
})
export class ArticuloRestService {

  constructor(private httpClient: HttpClient) { }

  public buscarTodos(): Observable<Articulo[]> {

    return this.httpClient.get<Articulo[]>("http://localhost:4200/api/articulos");
  }

  public borrar(articulo: Articulo): Observable<Articulo> {
    return this.httpClient.post<Articulo>(`http://localhost:4200/api/articulos/borrar/${articulo.id}`, articulo)

  }

  public activar(articulo: Articulo): Observable<Articulo> {
    return this.httpClient.post<Articulo>(`http://localhost:4200/api/articulos/activar/${articulo.id}`, articulo)

  }

  public insertar(articulo: Articulo): Observable<Articulo> {

    return this.httpClient.post<Articulo>("http://localhost:4200/api/articulos", articulo)

  }

  public editar(articulo: Articulo): Observable<Articulo> {

    return this.httpClient.put<Articulo>("http://localhost:4200/api/articulos/", articulo)
  }

  public agregarCarrito(id: number): Observable<Articulo[]> {

    return this.httpClient.post<Articulo[]>("http://localhost:4200/api/articulos/", id)

  }

  public verDetalle(id: number): Observable<Articulo> {
    return this.httpClient.get<Articulo>(`http://localhost:4200/api/articulos/${id}`)
  }

}