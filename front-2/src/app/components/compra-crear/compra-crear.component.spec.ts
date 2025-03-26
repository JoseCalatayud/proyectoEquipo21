import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompraCrearComponent } from './compra-crear.component';

describe('CompraCrearComponent', () => {
  let component: CompraCrearComponent;
  let fixture: ComponentFixture<CompraCrearComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompraCrearComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompraCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
