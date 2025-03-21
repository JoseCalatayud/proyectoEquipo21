import { HttpInterceptorFn } from '@angular/common/http';
import { AutenticacionService } from './autenticacion.service';
import { inject } from '@angular/core';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  console.log("soy el interceptor mas majo");
  console.log("la url es " + req.url);
  //req.headers.set("Authorization", "admin:admin123");

  const authService = inject(AutenticacionService);
  let clonedRequest=null;

    console.log("el token" +authService.autorizacion);
  
    //console.log("token1"+"YWRtaW46YWRtaW4xMjM=")
    
     clonedRequest = req.clone({
    

      setHeaders: {
       Authorization: "Basic " +authService.autorizacion
       //Authorization: "Basic YWRtaW46YWRtaW4xMjM="
      }
    });
    console.log(clonedRequest.headers.get("Authorization"));
    return next(clonedRequest);


    return next(clonedRequest);
  

};
