import { TestBed } from '@angular/core/testing';

import { HistoricoVentaRestService } from './historico-venta-rest.service';

describe('HistoricoVentaRestService', () => {
  let service: HistoricoVentaRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HistoricoVentaRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
