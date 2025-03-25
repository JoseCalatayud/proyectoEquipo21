import { HttpInterceptorFn } from '@angular/common/http';
import { AutenticacionService } from './autenticacion.service';
import { inject } from '@angular/core';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const authService = inject(AutenticacionService);
  let clonedRequest = req;

  if (authService.autorizacion) {
    clonedRequest = req.clone({
      setHeaders: {
        Authorization: "Basic " + authService.autorizacion
      }
    });
  }

  return next(clonedRequest);
};
