import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ventas-menu',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './ventas-menu.component.html',
  styleUrls: ['./ventas-menu.component.css'] // Reutilizar estilos
})
export class VentasMenuComponent {}
