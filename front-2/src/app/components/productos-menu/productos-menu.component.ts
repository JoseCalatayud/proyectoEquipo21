import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-productos-menu',
  standalone: true,
  imports: [CommonModule, RouterModule],
  providers: [AuthService],
  templateUrl: './productos-menu.component.html',
  styleUrls: ['./productos-menu.component.css']
})
export class ProductosMenuComponent implements OnInit {
  isAdmin: boolean = false;
  username: string = '';

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    const user = this.authService.currentUserValue;
    if (user) {
      this.username = user.username;
      this.isAdmin = user.roles && user.roles.some((role: any) =>
        role.authority === 'ROLE_ADMIN' || role === 'ROLE_ADMIN');
    }
  }
}
