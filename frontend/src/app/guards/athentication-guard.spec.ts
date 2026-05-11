import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { athenticationGuard } from './athentication-guard';

describe('athenticationGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => athenticationGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
