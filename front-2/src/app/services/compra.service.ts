import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.prod';

export interface DetalleCompra {
  idArticulo: number;
  nombreArticulo: string;
  descripcionArticulo: string;
  codigoBarrasArticulo: string;
  familiaArticulo: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface Compra {
  id: number;
  fecha: string;
  total: number;
  usuario: string;
  detalles: DetalleCompra[];
}

export interface DetalleCompraRequest {
  idArticulo: number;
  cantidad: number;
  precioUnitario: number;
}

export interface CompraRequest {
  detalles: DetalleCompraRequest[];
}

@Injectable({
  providedIn: 'root'
})
export class CompraService {

  constructor(private http: HttpClient) { }

  obtenerHistorialCompras(): Observable<Compra[]> {
    return this.http.get<Compra[]>(`${environment.apiUrl}/compras`);
  }

  obtenerCompraPorId(id: number): Observable<Compra> {
    return this.http.get<Compra>(`${environment.apiUrl}/compras/${id}`);
  }

  crearCompra(compraRequest: CompraRequest): Observable<Compra> {
    return this.http.post<Compra>(`${environment.apiUrl}/compras`, compraRequest);
  }

  anularCompra(id: number): Observable<any> {
    return this.http.delete<any>(`${environment.apiUrl}/compras/${id}`);
  }

  buscarComprasPorFechas(fechaInicio: string, fechaFin: string): Observable<Compra[]> {
    return this.http.get<Compra[]>(`${environment.apiUrl}/compras/fechas?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);
  }
}
