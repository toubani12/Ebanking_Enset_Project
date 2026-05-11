import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/Auth/auth-service';

export const appHttpInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  if(!req.url.includes('/auth/login') && authService.isAuthenticated) {
    const newRequest = req.clone({
      headers: req.headers.set('Authorization', 'Bearer ' + authService.accesTokken)
    });
    return next(newRequest);
  }else
  

  return next(req);
};
