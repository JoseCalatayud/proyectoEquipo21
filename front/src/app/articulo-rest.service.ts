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

  public borrar(articulo: Articulo): Observable<Articulo[]> {
    return this.httpClient.delete<Articulo[]>(`http://localhost:4200/api/articulos/${articulo.id}`)

  }

  //public editar(articulo: Articulo): Observable<Articulo> {

  //   return this.httpClient.post<Articulo>(`http://localhost:4200/api/articulos/${articulo.id}`)
  // }

}