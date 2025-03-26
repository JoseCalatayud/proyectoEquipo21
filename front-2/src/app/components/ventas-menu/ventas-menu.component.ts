import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ventas-menu',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="ventas-menu">
      <header class="menu-header">
        <h1>Gestión de Ventas</h1>
        <button class="btn btn-secondary" routerLink="/dashboard">
          Volver al Dashboard
        </button>
      </header>

      <div class="menu-content">
        <h2>Seleccione una opción</h2>

        <div class="menu-cards">
          <div class="menu-card" routerLink="/ventas/productos">
            <div class="icon">🛍️</div>
            <h3>Realizar Venta</h3>
            <p>Seleccionar productos y procesar una nueva venta</p>
          </div>

          <div class="menu-card" routerLink="/ventas/historial">
            <div class="icon">📋</div>
            <h3>Historial de Ventas</h3>
            <p>Ver las ventas realizadas anteriormente</p>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['../productos-menu/productos-menu.component.css'] // Reutilizar estilos
})
export class VentasMenuComponent {}
