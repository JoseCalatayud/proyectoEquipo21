import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VentaService, Venta } from '../../services/venta.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-ventas-historial',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, DatePipe],
  templateUrl: './ventas-historial.component.html',
  styleUrls: ['./ventas-historial.component.css']
})
export class VentasHistorialComponent implements OnInit {
  ventas: Venta[] = [];
  loading: boolean = true;
  error: string = '';
  isAdmin: boolean = false;
  filtroFechaInicio: string = '';
  filtroFechaFin: string = '';

  constructor(
    private ventaService: VentaService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarRolUsuario();
    this.cargarVentas();
  }

  cargarRolUsuario(): void {
    const currentUser = this.authService.currentUserValue;
    if (currentUser && currentUser.roles) {
      this.isAdmin = currentUser.roles.some((role: any) =>
        role.authority === 'ROLE_ADMIN' || role === 'ROLE_ADMIN');
    }
  }

  cargarVentas(): void {
    this.loading = true;
    this.ventaService.obtenerHistorialVentas().subscribe({
      next: (data) => {
        this.ventas = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error al cargar historial de ventas: ' +
          (err.error?.mensaje || 'No se pudo conectar con el servidor');
        this.loading = false;
        console.error('Error al cargar ventas:', err);
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

      this.ventaService.buscarVentasPorFechas(
        fechaInicio.toISOString(),
        fechaFin.toISOString()
      ).subscribe({
        next: (data) => {
          this.ventas = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Error al filtrar ventas: ' +
            (err.error?.mensaje || 'No se pudo conectar con el servidor');
          this.loading = false;
        }
      });
    }
  }

  limpiarFiltros(): void {
    this.filtroFechaInicio = '';
    this.filtroFechaFin = '';
    this.cargarVentas();
  }

  anularVenta(ventaId: number): void {
    if (confirm('¿Estás seguro de que deseas anular esta venta? Esta acción no se puede deshacer.')) {
      this.loading = true;
      this.ventaService.anularVenta(ventaId).subscribe({
        next: () => {
          this.cargarVentas();
        },
        error: (err) => {
          this.error = 'Error al anular la venta: ' +
            (err.error?.mensaje || 'No se pudo conectar con el servidor');
          this.loading = false;
        }
      });
    }
  }
}
