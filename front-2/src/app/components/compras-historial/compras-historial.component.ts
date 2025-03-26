import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CompraService, Compra } from '../../services/compra.service';

@Component({
  selector: 'app-compras-historial',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, DatePipe],
  templateUrl: './compras-historial.component.html',
  styleUrls: ['./compras-historial.component.css']
})
export class ComprasHistorialComponent implements OnInit {
  compras: Compra[] = [];
  loading: boolean = true;
  error: string = '';
  filtroFechaInicio: string = '';
  filtroFechaFin: string = '';

  constructor(private compraService: CompraService) {}

  ngOnInit(): void {
    this.cargarCompras();
  }

  cargarCompras(): void {
    this.loading = true;
    this.compraService.obtenerHistorialCompras().subscribe({
      next: (data) => {
        this.compras = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error al cargar historial de compras: ' +
          (err.error?.mensaje || 'No se pudo conectar con el servidor');
        this.loading = false;
        console.error('Error al cargar compras:', err);
      }
    });
  }

  filtrarPorFechas(): void {
    if (this.filtroFechaInicio && this.filtroFechaFin) {
      this.loading = true;
      const fechaInicio = new Date(this.filtroFechaInicio);
      const fechaFin = new Date(this.filtroFechaFin);

      // Ajustar fecha fin al final del día
      fechaFin.setHours(23, 59, 59, 999);

      this.compraService.buscarComprasPorFechas(
        fechaInicio.toISOString(),
        fechaFin.toISOString()
      ).subscribe({
        next: (data) => {
          this.compras = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Error al filtrar compras: ' +
            (err.error?.mensaje || 'No se pudo conectar con el servidor');
          this.loading = false;
        }
      });
    }
  }

  limpiarFiltros(): void {
    this.filtroFechaInicio = '';
    this.filtroFechaFin = '';
    this.cargarCompras();
  }

  anularCompra(compraId: number): void {
    if (confirm('¿Estás seguro de que deseas anular esta compra? Esta acción no se puede deshacer.')) {
      this.loading = true;
      this.compraService.anularCompra(compraId).subscribe({
        next: () => {
          this.cargarCompras();
        },
        error: (err) => {
          this.error = 'Error al anular la compra: ' +
            (err.error?.mensaje || 'No se pudo conectar con el servidor');
          this.loading = false;
        }
      });
    }
  }
}
