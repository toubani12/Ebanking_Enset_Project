import { CanActivateFn, Router } from '@angular/router';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from '../services/Auth/auth-service';

export const athenticationGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  // Skip guard on server-side because localstorage is not available
  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  if (authService.isAuthenticated) {
    return true;
  } else {
    router.navigateByUrl('/login');
    return false;
  }
};
