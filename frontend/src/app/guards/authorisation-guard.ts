import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/Auth/auth-service';

export const authorisationGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.roles && authService.roles.includes('ADMIN')) {
    return true;
  } else {
    router.navigateByUrl('/admin/not-authorised');
    return false;
  }
};
