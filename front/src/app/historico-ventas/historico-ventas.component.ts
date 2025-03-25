import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DetalleVenta } from '../detalle-venta'; // Importamos DetalleVenta

interface Venta {
  id: number;
  total: number;
  usuario: {
    id: number;
    username: string;
  };
  detalles: DetalleVenta[]; // Usamos DetalleVenta[]
}

@Component({
  selector: 'app-historico-ventas',
  templateUrl: './historico-ventas.component.html',
  styleUrls: ['./historico-ventas.component.scss'],
  imports: [CommonModule, RouterLink],
  standalone: true
})
export class HistoricoVentasComponent implements OnInit {
  listaVentas: Venta[] = [];

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.cargarVentas();
  }

  cargarVentas() {
    const headers = new HttpHeaders({
      'Authorization': 'Basic ' + btoa('admin:admin123') // Reemplaza con tu lógica de autenticación
    });

    this.http.get<Venta[]>('/api/ventas/listar', { headers }).subscribe(
      (data) => {
        this.listaVentas = data;
      },
      (error) => {
        console.error('Error al cargar las ventas:', error);
      }
    );
  }
}
