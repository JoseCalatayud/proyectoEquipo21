import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VentasMenuComponent } from './ventas-menu.component';

describe('VentasMenuComponent', () => {
  let component: VentasMenuComponent;
  let fixture: ComponentFixture<VentasMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VentasMenuComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VentasMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
