import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComprasProductosComponent } from './compras-productos.component';

describe('ComprasProductosComponent', () => {
  let component: ComprasProductosComponent;
  let fixture: ComponentFixture<ComprasProductosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComprasProductosComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComprasProductosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
