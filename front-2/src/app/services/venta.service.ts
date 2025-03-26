import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DetalleVenta {
  idArticulo: number;
  nombreArticulo: string;
  descripcionArticulo: string;
  codigoBarrasArticulo: string;
  familiaArticulo: string;
  cantidad: number;
  subtotal: number;
}

export interface Usuario {
  id: number;
  username: string;
}

export interface Venta {
  id: number;
  fecha: string;
  total: number;
  usuario: Usuario;
  detalles: DetalleVenta[];
}

@Injectable({
  providedIn: 'root'
})
export class VentaService {

  constructor(private http: HttpClient) { }

  obtenerHistorialVentas(): Observable<Venta[]> {
    return this.http.get<Venta[]>('/api/ventas/listar');
  }

  obtenerVentaPorId(id: number): Observable<Venta> {
    return this.http.get<Venta>(`/api/ventas/${id}`);
  }

  crearVenta(detalles: { codigoBarras: string, cantidad: number }[]): Observable<Venta> {
    return this.http.post<Venta>('/api/ventas/crear', { detalles });
  }

  anularVenta(id: number): Observable<any> {
    return this.http.delete<any>(`/api/ventas/${id}`);
  }

  buscarVentasPorFechas(fechaInicio: string, fechaFin: string): Observable<Venta[]> {
    return this.http.get<Venta[]>(`/api/ventas/fechas?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);
  }
}
