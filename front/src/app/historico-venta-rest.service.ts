import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Articulo } from './articulo';
import { setThrowInvalidWriteToSignalError } from '@angular/core/primitives/signals';
import { DetalleVenta } from './detalle-venta';

//testdb nombre de la BBDD

@Injectable({
  providedIn: 'root'
})
export class detalleVentaRestService {

  constructor(private httpClient: HttpClient) { }

  public buscarTodos(): Observable<DetalleVenta[]> {

    return this.httpClient.get<Articulo[]>("http://localhost:4200/api/articulos");
  }
}