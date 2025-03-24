import { Component } from '@angular/core';
import { ArticuloRestService } from '../articulo-rest.service';
import { ActivatedRoute } from '@angular/router';
import { Articulo } from '../articulo';

@Component({
  selector: 'app-detalle',
  imports: [],
  templateUrl: './detalle.component.html',
  styleUrl: './detalle.component.scss'
})
export class DetalleComponent {
  articuloDetalle: Articulo = {} as Articulo;

  constructor(private articuloRestService: ArticuloRestService, private rutaActiva: ActivatedRoute) {



    let id = this.rutaActiva.snapshot.paramMap.get('id');


    if (id != null)
      this.articuloRestService.verDetalle(parseInt(id)).subscribe((datos: Articulo) => {

      this.articuloDetalle=datos;
      })

  }

}
