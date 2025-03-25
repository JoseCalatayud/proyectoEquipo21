// /Users/andrescalderon/Desktop/Valeria/Ascender/proyectoEquipo21/front/src/app/historico-ventas/historico-ventas.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { VentaRestService, Venta } from '../venta-rest.service'; // Importamos VentaRestService y Venta

@Component({
  selector: 'app-historico-ventas',
  templateUrl: './historico-ventas.component.html',
  styleUrls: ['./historico-ventas.component.scss'],
  imports: [CommonModule, RouterLink],
  standalone: true // Agregado: standalone: true
})
export class HistoricoVentasComponent implements OnInit {
  listaVentas: Venta[] = [];

  constructor(private ventaRestService: VentaRestService) { } // Inyectamos VentaRestService

  ngOnInit(): void {
    this.cargarVentas();
  }

  cargarVentas() {
    this.ventaRestService.listarVentas().subscribe({
      next: (data) => {
        this.listaVentas = data;
      },
      error: (error) => {
        console.error('Error al cargar las ventas:', error);
      }
    });
  }
}
