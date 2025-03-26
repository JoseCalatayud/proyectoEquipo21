import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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
    return this.http.get<Compra[]>('/api/compras');
  }

  obtenerCompraPorId(id: number): Observable<Compra> {
    return this.http.get<Compra>(`/api/compras/${id}`);
  }

  crearCompra(compraRequest: CompraRequest): Observable<Compra> {
    return this.http.post<Compra>('/api/compras', compraRequest);
  }

  anularCompra(id: number): Observable<any> {
    return this.http.delete<any>(`/api/compras/${id}`);
  }

  buscarComprasPorFechas(fechaInicio: string, fechaFin: string): Observable<Compra[]> {
    return this.http.get<Compra[]>(`/api/compras/fechas?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);
  }
}
