import { TestBed } from '@angular/core/testing';

import { ArticuloRestService } from './articulo-rest.service';

describe('ArticuloRestService', () => {
  let service: ArticuloRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ArticuloRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
