import { Component, OnInit } from '@angular/core';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductoService, Producto } from '../../services/producto.service';
import { AuthService } from '../../services/auth.service';
import { CarritoService } from '../../services/carrito.service';

@Component({
  selector: 'app-productos-lista',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './productos-lista.component.html',
  styleUrls: ['./productos-lista.component.css']
})
export class ProductosListaComponent implements OnInit {
  productos: Producto[] = [];
  productosFiltrados: Producto[] = [];
  isAdmin: boolean = false;
  loading: boolean = true;
  error: string = '';
  filtroNombre: string = '';
  filtroFamilia: string = '';
  familias: string[] = [];
  cantidadSeleccionada: { [key: number]: number } = {};
  esContextoVentas: boolean = false;

  constructor(
    private productoService: ProductoService,
    private authService: AuthService,
    public carritoService: CarritoService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.esContextoVentas = this.router.url.includes('/ventas/productos');
    this.cargarUsuario();
    this.cargarProductos();
  }

  cargarUsuario(): void {
    const user = this.authService.currentUserValue;
    if (user) {
      this.isAdmin = user.roles && user.roles.some((role: any) =>
        role.authority === 'ROLE_ADMIN' || role === 'ROLE_ADMIN');
    } else {
      this.router.navigate(['/login']);
    }
  }

  cargarProductos(): void {
    this.loading = true;
    this.productoService.listarProductos().subscribe({
      next: (data) => {
        console.log('Productos recibidos:', data);
        this.productos = data;
        this.productosFiltrados = [...data];
        this.productos.forEach(producto => {
          this.cantidadSeleccionada[producto.id] = 1;
        });
        this.extraerFamilias();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar productos:', err);
        this.error = 'Error al cargar los productos. Por favor, inténtelo de nuevo.';
        this.loading = false;
      }
    });
  }

  extraerFamilias(): void {
    const familiasSet = new Set<string>();
    this.productos.forEach(producto => {
      if (producto.familia) {
        familiasSet.add(producto.familia);
      }
    });
    this.familias = Array.from(familiasSet).sort();
  }

  filtrarProductos(): void {
    this.productosFiltrados = this.productos.filter(producto => {
      const coincideNombre = this.filtroNombre ?
        producto.nombre.toLowerCase().includes(this.filtroNombre.toLowerCase()) : true;

      const coincideFamilia = this.filtroFamilia ?
        producto.familia === this.filtroFamilia : true;

      return coincideNombre && coincideFamilia;
    });
  }
  toggleEstadoProducto(producto: Producto): void {
    this.loading = true;

    if (producto.borrado) {
      this.productoService.activarProducto(producto.id).subscribe({
        next: () => {
          producto.borrado = false;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Error al activar el producto';
          this.loading = false;
          console.error('Error:', err);
        }
      });
    } else {
      this.productoService.desactivarProducto(producto.id).subscribe({
        next: () => {
          producto.borrado = true;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Error al desactivar el producto';
          this.loading = false;
          console.error('Error:', err);
        }
      });
    }
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroFamilia = '';
    this.productosFiltrados = [...this.productos];
  }

  volver(): void {
    this.router.navigate(['/productos']);
  }

  agregarAlCarrito(producto: Producto): void {
    const cantidad = this.cantidadSeleccionada[producto.id] || 1;

    if (cantidad > producto.stock) {
      this.error = `No hay suficiente stock de ${producto.nombre}. Stock disponible: ${producto.stock}`;
      return;
    }

    this.carritoService.agregarProducto(producto, cantidad);
    this.cantidadSeleccionada[producto.id] = 1;
  }

  irAlCarrito(): void {
    this.router.navigate(['/ventas/carrito']);
  }
}
