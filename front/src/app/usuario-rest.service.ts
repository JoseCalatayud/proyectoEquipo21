import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario } from './usuario';
import { AutenticacionService } from './autenticacion.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class UsuarioRestService {


  constructor(private httpClient: HttpClient,private autenticacionService:AutenticacionService, private router: Router) { }

  public validar(usuario:string,clave:string) {

      this.autenticacionService.autorizacion=btoa(usuario + ":"+ clave);
      
       this.httpClient.post("http://localhost:4200/api/login",null).subscribe( (datos:any)=> {
          console.log(datos);
          this.router.navigate(['/listaArticulos']);
      }, (error:Error)=> {

        console.log("ha petado");
      })
  }
}
