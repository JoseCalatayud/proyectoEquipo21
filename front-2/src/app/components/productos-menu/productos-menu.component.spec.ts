import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductosmenuComponent } from './productos-menu.component';

describe('ProductosmenuComponent', () => {
  let component: ProductosmenuComponent;
  let fixture: ComponentFixture<ProductosmenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductosmenuComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductosmenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
