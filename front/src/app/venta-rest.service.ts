// /Users/andrescalderon/Desktop/Valeria/Ascender/proyectoEquipo21/front/src/app/venta-rest.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

// Definimos la interfaz Venta (la misma que usas en HistoricoVentasComponent)
export interface Venta {
  id: number;
  total: number;
  usuario: {
    id: number;
    username: string;
  };
  detalles: DetalleVenta[];
}
export class DetalleVenta {

  constructor(
    public idArticulo: number,
    public nombreArticulo: string,
    public descripcionArticulo: string,
    public codigoBarrasArticulo: string,
    public familiaArticulo: string,
    public cantidad: number,
    public subtotal: number
  ) {}
}

@Injectable({
  providedIn: 'root'
})
export class VentaRestService {

  constructor(private httpClient: HttpClient) { }

  public listarVentas(): Observable<Venta[]> {
    const headers = new HttpHeaders({
      'Authorization': 'Basic ' + btoa('admin:admin123') // Reemplaza con tu lógica de autenticación
    });
    return this.httpClient.get<Venta[]>('/api/ventas/listar', { headers });
  }
}
