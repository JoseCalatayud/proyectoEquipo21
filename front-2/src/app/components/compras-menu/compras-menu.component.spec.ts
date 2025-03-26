import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComprasMenuComponent } from './compras-menu.component';

describe('ComprasMenuComponent', () => {
  let component: ComprasMenuComponent;
  let fixture: ComponentFixture<ComprasMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComprasMenuComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComprasMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
