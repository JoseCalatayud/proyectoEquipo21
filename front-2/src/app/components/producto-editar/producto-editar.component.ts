import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProductoService, Producto } from '../../services/producto.service';

@Component({
  selector: 'app-producto-editar',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './producto-editar.component.html',
  styleUrls: ['./producto-editar.component.css']
})
export class ProductoEditarComponent implements OnInit {
  productoForm: FormGroup;
  loading = false;
  error = '';
  id: number;
  familias = ['Informática', 'Electrónica', 'Telefonía', 'Audio/Video', 'Accesorios'];
  producto: Producto | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private productoService: ProductoService
  ) {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.productoForm = this.formBuilder.group({
      nombre: ['', [Validators.required]],
      descripcion: ['', [Validators.required]],
      familia: ['', [Validators.required]],
      fotografia: [''],
      precioVenta: ['', [Validators.required, Validators.min(0.01)]]
    });
  }

  ngOnInit(): void {
    this.cargarProducto();
  }

  cargarProducto(): void {
    this.loading = true;
    this.productoService.obtenerProductoPorId(this.id).subscribe({
      next: (producto) => {
        this.producto = producto;
        this.productoForm.patchValue({
          nombre: producto.nombre,
          descripcion: producto.descripcion,
          familia: producto.familia,
          fotografia: producto.fotografia,
          precioVenta: producto.precioVenta
        });
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Error al cargar el producto';
        this.loading = false;
        console.error('Error al cargar producto:', error);
      }
    });
  }

  onSubmit(): void {
    if (this.productoForm.invalid || !this.producto) {
      return;
    }

    this.loading = true;
    this.error = '';

    this.productoService.actualizarProducto(this.id, this.productoForm.value)
      .subscribe({
        next: () => {
          this.router.navigate(['/productos/listar']);
        },
        error: (error) => {
          this.error = error.error?.mensaje || 'Error al actualizar el producto';
          this.loading = false;
          console.error('Error al actualizar producto:', error);
        }
      });
  }

  volver(): void {
    this.router.navigate(['/productos/listar']);
  }
}
