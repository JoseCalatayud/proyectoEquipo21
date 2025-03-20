import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  console.log("soy el interceptor mas majo");
  console.log("la url es " + req.url);
  //req.headers.set("Authorization", "admin:admin123");


  const codificado = btoa("admin123");

  console.log("Codificado:", codificado);


  if (req.url != "http://localhost:4200/login") {

    const clonedRequest = req.clone({

      setHeaders: {
        Authorization: "Basic YWRtaW46YWRtaW4xMjM="
      }
    });
    console.log(clonedRequest.headers.get("Authorization"));
    return next(clonedRequest);

  } else {
    return next(req);
  }

};
