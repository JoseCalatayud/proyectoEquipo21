import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario } from './usuario';

@Injectable({
  providedIn: 'root'
})
export class UsuarioRestService {


  constructor(private httpClient: HttpClient) { }

  public validar() {

    console.log("llega");

      const headers = new HttpHeaders({
        'Authorization': 'Basic admin admin123',
      });
  
      const req = new HttpRequest('GET', "http://localhost:4200/login", {
        headers: headers
      });
  
       this.httpClient.request(req).subscribe( (datos:any)=> {

        console.log(datos);

      })
  }
}
