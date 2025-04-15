import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { environment } from '../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const currentUser = authService.currentUserValue;

  // Si es una petición a /api, redirigirla a la URL del backend definida en environment
  if (req.url.startsWith('/api')) {
    // Quitar /api del principio para evitar duplicación
    const apiPath = req.url.replace('/api', '');
    
    // Crear nueva URL con la base correcta
    const apiUrl = environment.apiUrl;
    
    // Clonar la petición con la nueva URL
    req = req.clone({
      url: `${apiUrl}${apiPath}`
    });
  }

  // Añadir encabezados de autenticación si el usuario está logueado
  if (currentUser && currentUser.username && currentUser.password) {
    req = req.clone({
      setHeaders: {
        Authorization: `Basic ${btoa(currentUser.username + ':' + currentUser.password)}`
      }
    });
  }

  return next(req);
};
