import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const AdminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const currentUser = authService.currentUserValue;

  // Verificar si el usuario es admin
  if (currentUser && currentUser.roles &&
      currentUser.roles.some((role: any) =>
        role.authority === 'ROLE_ADMIN' || role === 'ROLE_ADMIN')) {
    return true;
  }

  // No es admin, redirigir al menú de productos
  router.navigate(['/productos']);
  return false; // Añadir esta línea para solucionar el error
};
