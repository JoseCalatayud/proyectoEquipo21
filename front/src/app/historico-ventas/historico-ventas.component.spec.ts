import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoricoVentasComponent } from './historico-ventas.component';

describe('HistoricoVentasComponent', () => {
  let component: HistoricoVentasComponent;
  let fixture: ComponentFixture<HistoricoVentasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoricoVentasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistoricoVentasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
