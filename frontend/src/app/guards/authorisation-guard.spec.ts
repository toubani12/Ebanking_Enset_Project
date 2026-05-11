import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { authorisationGuard } from './authorisation-guard';

describe('authorisationGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => authorisationGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
