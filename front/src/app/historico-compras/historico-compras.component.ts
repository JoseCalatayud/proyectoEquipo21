import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CompraRestService } from '../compra-rest.service';
import { Compra } from '../compra'; // Importamos la clase Compra

@Component({
  selector: 'app-historico-compras',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './historico-compras.component.html',
  styleUrl: './historico-compras.component.scss'
})
export class HistoricoComprasComponent implements OnInit {
  listaCompras: Compra[] = [];

  constructor(private compraRestService: CompraRestService) { }

  ngOnInit(): void {
    this.cargarCompras();
  }

  cargarCompras() {
    this.compraRestService.listarCompras().subscribe({
      next: (data) => {
        this.listaCompras = data;
      },
      error: (error) => {
        console.error('Error al cargar las compras:', error);
      }
    });
  }
}
