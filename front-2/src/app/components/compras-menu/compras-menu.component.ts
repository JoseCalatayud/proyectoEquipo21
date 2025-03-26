import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-compras-menu',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './compras-menu.component.html',
  styleUrls: ['./compras-menu.component.css']
})
export class ComprasMenuComponent {
  username: string = '';

  constructor() {
    // Recuperar el nombre de usuario del localStorage si es necesario
    const userStr = localStorage.getItem('currentUser');
    if (userStr) {
      const user = JSON.parse(userStr);
      this.username = user.username;
    }
  }
}
