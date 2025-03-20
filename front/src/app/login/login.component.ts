import { Component, OnInit } from '@angular/core';
import { UsuarioRestService } from '../usuario-rest.service';
import { Usuario } from '../usuario';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgFor } from '@angular/common';
import { AutenticacionService } from '../autenticacion.service';
@Component({
  selector: 'app-login',
  imports: [RouterLink, FormsModule, NgFor, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  usuario:string="";
  clave:string="";

  constructor (private autorizacionService:AutenticacionService,private usuarioRestService:UsuarioRestService){
    


  }

  validar() {

   console.log("hola");
   this.usuarioRestService.validar();
  }

}
