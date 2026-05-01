import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth.service';

export const AuthInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Retrieve the access token from localStorage
  const accessToken = localStorage.getItem('accessToken');

  console.log('Intercepting request with token:', accessToken); // Debugging

  // Clone the request and add the Authorization header if the token exists
  if (accessToken) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${accessToken}`,
      },
    });
  }

  // Handle the request and catch 401 errors
  return next(req).pipe(
    catchError((error) => {
      if (error.status === 401 && !authService.isRefreshing) {
        console.log('Intercepted 401 error, attempting to refresh token'); // Debugging
        authService.isRefreshing = true;

        // Attempt to refresh the token
        return authService.refreshToken().pipe(
          switchMap((response: any) => {
            console.log('Token refresh successful:', response); // Debugging

            // Update the access token in localStorage
            localStorage.setItem('accessToken', response.accessToken);

            // Clone the original request with the new token
            const newReq = req.clone({
              setHeaders: {
                Authorization: `Bearer ${response.accessToken}`,
              },
            });

            // Retry the original request with the new token
            return next(newReq);
          }),
          catchError((refreshError) => {
            console.error('Token refresh failed:', refreshError); // Debugging
            authService.isRefreshing = false;

            // Logout and redirect to login if refresh fails
            authService.logout();
            router.navigate(['/signin']);
            return throwError(refreshError);
          })
        );
      }

      // If not a 401 error, rethrow the error
      return throwError(error);
    })
  );
};