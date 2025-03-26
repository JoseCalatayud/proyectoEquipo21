import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProductoService } from '../../services/producto.service';

@Component({
  selector: 'app-producto-crear',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './producto-crear.component.html',
  styleUrls: ['./producto-crear.component.css']
})
export class ProductoCrearComponent implements OnInit {
  productoForm: FormGroup;
  loading = false;
  error = '';
  familias = ['Informática', 'Electrónica', 'Telefonía', 'Audio/Video', 'Accesorios'];

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private productoService: ProductoService
  ) {
    this.productoForm = this.formBuilder.group({
      nombre: ['', [Validators.required]],
      descripcion: ['', [Validators.required]],
      codigoBarras: ['', [Validators.required, Validators.pattern('^[0-9]{12,13}$')]],
      familia: ['', [Validators.required]],
      fotografia: [''],
      precioVenta: ['', [Validators.required, Validators.min(0.01)]]
    });
  }

  ngOnInit(): void {
  }

  onSubmit() {
    if (this.productoForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';

    this.productoService.crearProducto(this.productoForm.value)
      .subscribe({
        next: () => {
          this.router.navigate(['/productos/listar']);
        },
        error: error => {
          this.error = error.error?.mensaje || 'Error al crear el producto';
          this.loading = false;
          console.error('Error al crear producto:', error);
        }
      });
  }

  volver() {
    this.router.navigate(['/productos']);
  }
}
