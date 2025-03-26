import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComprasHistorialComponent } from './compras-historial.component';

describe('ComprasHistorialComponent', () => {
  let component: ComprasHistorialComponent;
  let fixture: ComponentFixture<ComprasHistorialComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComprasHistorialComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComprasHistorialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
