import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CompraService, CompraRequest, DetalleCompraRequest } from '../../services/compra.service';
import { ProductoService, Producto } from '../../services/producto.service';

@Component({
  selector: 'app-compra-crear',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './compra-crear.component.html',
  styleUrls: ['./compra-crear.component.css']
})
export class CompraCrearComponent implements OnInit {
  productos: Producto[] = [];
  filtroNombre: string = '';
  productosFiltrados: Producto[] = [];
  detallesCompra: {
    producto: Producto,
    cantidad: number,
    precioUnitario: number,
    subtotal: number
  }[] = [];
  loading: boolean = false;
  loadingProductos: boolean = true;
  error: string = '';
  exito: boolean = false;
  totalCompra: number = 0;

  constructor(
    private productoService: ProductoService,
    private compraService: CompraService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarProductos();
  }

  cargarProductos(): void {
    this.loadingProductos = true;
    this.productoService.listarProductos().subscribe({
      next: (data) => {
        this.productos = data;
        this.productosFiltrados = [...data];
        this.loadingProductos = false;
      },
      error: (err) => {
        this.error = 'Error al cargar los productos: ' +
          (err.error?.mensaje || 'No se pudo conectar con el servidor');
        this.loadingProductos = false;
      }
    });
  }

  filtrarProductos(): void {
    if (this.filtroNombre.trim() === '') {
      this.productosFiltrados = [...this.productos];
    } else {
      const filtro = this.filtroNombre.toLowerCase().trim();
      this.productosFiltrados = this.productos.filter(producto =>
        producto.nombre.toLowerCase().includes(filtro) ||
        producto.codigoBarras.toLowerCase().includes(filtro)
      );
    }
  }

  agregarDetalle(producto: Producto): void {
    // Verificar si el producto ya está en la lista
    const index = this.detallesCompra.findIndex(d => d.producto.id === producto.id);

    if (index >= 0) {
      // Actualizar cantidad
      this.detallesCompra[index].cantidad++;
      this.detallesCompra[index].subtotal =
        this.detallesCompra[index].cantidad * this.detallesCompra[index].precioUnitario;
    } else {
      // Agregar nuevo detalle
      this.detallesCompra.push({
        producto,
        cantidad: 1,
        precioUnitario: producto.precioVenta,
        subtotal: producto.precioVenta
      });
    }

    this.calcularTotal();
  }

  eliminarDetalle(index: number): void {
    this.detallesCompra.splice(index, 1);
    this.calcularTotal();
  }

  actualizarCantidad(index: number): void {
    if (this.detallesCompra[index].cantidad <= 0) {
      this.detallesCompra[index].cantidad = 1;
    }
    this.detallesCompra[index].subtotal =
      this.detallesCompra[index].cantidad * this.detallesCompra[index].precioUnitario;
    this.calcularTotal();
  }

  actualizarPrecio(index: number): void {
    if (this.detallesCompra[index].precioUnitario <= 0) {
      this.detallesCompra[index].precioUnitario = 0.01;
    }
    this.detallesCompra[index].subtotal =
      this.detallesCompra[index].cantidad * this.detallesCompra[index].precioUnitario;
    this.calcularTotal();
  }

  calcularTotal(): void {
    this.totalCompra = this.detallesCompra.reduce((sum, detalle) => sum + detalle.subtotal, 0);
  }

  realizarCompra(): void {
    if (this.detallesCompra.length === 0) {
      this.error = 'Debe agregar al menos un producto a la compra';
      return;
    }

    this.loading = true;
    this.error = '';

    const compraRequest: CompraRequest = {
      detalles: this.detallesCompra.map(detalle => ({
        idArticulo: detalle.producto.id,
        cantidad: detalle.cantidad,
        precioUnitario: detalle.precioUnitario
      }))
    };

    this.compraService.crearCompra(compraRequest).subscribe({
      next: (respuesta) => {
        this.loading = false;
        this.exito = true;
        this.detallesCompra = [];
        this.totalCompra = 0;
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.mensaje || 'Error al realizar la compra';
      }
    });
  }

  volverAlMenu(): void {
    this.router.navigate(['/compras']);
  }

  nuevaCompra(): void {
    this.exito = false;
    this.error = '';
    this.detallesCompra = [];
    this.totalCompra = 0;
  }
}
