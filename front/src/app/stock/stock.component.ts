import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { ArticuloRestService } from '../articulo-rest.service';
import { Articulo } from '../articulo';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgFor } from '@angular/common';
import { ArticuloUpdateRequest } from '../articulo-update-request'; // Importado

declare var bootstrap: any;

@Component({
  selector: 'app-stock',
  imports: [RouterLink, FormsModule, NgFor, CommonModule, FormsModule],
  templateUrl: './stock.component.html',
  styleUrls: ['./stock.component.scss'],
  standalone: true
})
export class StockComponent {
  @ViewChild('editModal') editModal!: ElementRef;

  listaArticulos: Articulo[] = [];
  articuloSeleccionado: Articulo = new Articulo();

  constructor(private articuloRestService: ArticuloRestService) {
    this.cargarArticulos();
  }

  cargarArticulos() {
    this.articuloRestService.buscarTodos().subscribe((datos) => {
      this.listaArticulos = datos;
    })
  }

  activo(articulo: Articulo) {
    if (articulo.borrado) {
      return "No disponible";
    } else {
      return "Disponible";
    }
  }

  borrar(articulo: Articulo) {
    this.articuloRestService.borrar(articulo).subscribe((datos) => {
      this.cargarArticulos();
    })
  }

  activar(articulo: Articulo) {
    this.articuloRestService.activar(articulo).subscribe((datos) =>
      this.cargarArticulos()
    )
  }

  agregarCarrito(id: number) {
    this.articuloRestService.agregarCarrito(id).subscribe((datos) => {
      this.listaArticulos = datos;
    })
  }

  openEditModal(articulo: Articulo) {
    this.articuloSeleccionado = { ...articulo };
    const modal = new bootstrap.Modal(this.editModal.nativeElement);
    modal.show();
  }

  guardarCambios() {
    // Crear un objeto ArticuloUpdateRequest
    const articuloUpdateRequest: ArticuloUpdateRequest = {
      nombre: this.articuloSeleccionado.nombre,
      descripcion: this.articuloSeleccionado.descripcion,
      familia: this.articuloSeleccionado.familia,
      fotografia: this.articuloSeleccionado.fotografia,
      precioVenta: this.articuloSeleccionado.precioVenta
    };

    this.articuloRestService.editar(this.articuloSeleccionado.id, articuloUpdateRequest).subscribe({ // Modificado
      next: (datos) => {
        console.log('Articulo editado correctamente:', datos);
        alert("Articulo editado correctamente");
        this.cargarArticulos();
      },
      error: (error) => {
        console.error('Error al editar el articulo:', error);
        alert("Error al editar el articulo");
      }
    });
  }
}
