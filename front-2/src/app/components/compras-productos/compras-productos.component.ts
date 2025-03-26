// src/app/components/compras-productos/compras-productos.component.ts
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductoService, Producto } from '../../services/producto.service';
import { CarritoCompraService } from '../../services/carrito-compra.service';

@Component({
  selector: 'app-compras-productos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './compras-productos.component.html',
  styleUrls: ['./compras-productos.component.css']
})
export class ComprasProductosComponent implements OnInit {
  productos: Producto[] = [];
  productosFiltrados: Producto[] = [];
  loading: boolean = true;
  error: string = '';
  filtroNombre: string = '';
  filtroFamilia: string = '';
  familias: string[] = [];

  cantidadSeleccionada: { [key: number]: number } = {};
  precioSeleccionado: { [key: number]: number } = {};

  constructor(
    private productoService: ProductoService,
    public carritoCompraService: CarritoCompraService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarProductos();
  }

  cargarProductos(): void {
    this.loading = true;
    this.productoService.listarProductos().subscribe({
      next: (data) => {
        this.productos = data.filter(p => !p.borrado); // Mostrar solo activos
        this.productosFiltrados = [...this.productos];

        // Inicializar cantidades y precios
        this.productos.forEach(producto => {
          this.cantidadSeleccionada[producto.id] = 1;
          this.precioSeleccionado[producto.id] = producto.precioVenta * 0.75; // Precio sugerido 25% menos
        });

        this.extraerFamilias();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error al cargar los productos. Por favor, inténtelo de nuevo.';
        this.loading = false;
        console.error('Error al cargar productos:', err);
      }
    });
  }

  extraerFamilias(): void {
    const familiasSet = new Set(this.productos.map(producto => producto.familia));
    this.familias = Array.from(familiasSet).sort();
  }

  filtrarProductos(): void {
    let productos = [...this.productos];

    if (this.filtroNombre) {
      const filtro = this.filtroNombre.toLowerCase();
      productos = productos.filter(
        p => p.nombre.toLowerCase().includes(filtro) ||
             p.codigoBarras.toLowerCase().includes(filtro)
      );
    }

    if (this.filtroFamilia) {
      productos = productos.filter(p => p.familia === this.filtroFamilia);
    }

    this.productosFiltrados = productos;
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroFamilia = '';
    this.productosFiltrados = [...this.productos];
  }

  volver(): void {
    this.router.navigate(['/compras']);
  }

  agregarAlCarrito(producto: Producto): void {
    const cantidad = this.cantidadSeleccionada[producto.id] || 1;
    const precio = this.precioSeleccionado[producto.id] || producto.precioVenta;

    this.carritoCompraService.agregarProducto(producto, cantidad, precio);

    // Resetear campos después de agregar
    this.cantidadSeleccionada[producto.id] = 1;
  }

  irAlCarrito(): void {
    this.router.navigate(['/compras/carrito']);
  }
}
