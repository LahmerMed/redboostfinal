import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common'; // Keep CommonModule
import { RouterModule } from '@angular/router'; // Added RouterModule import
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { AuthService } from './app/pages/auth/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule], // Keep all necessary imports
  template: `
    <router-outlet></router-outlet>
  `,
  styleUrls: ['./app.component.scss'],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppComponent implements OnInit {
  constructor(private authService: AuthService, private router: Router) {}

  // app.component.ts
ngOnInit() {
  const accessToken = localStorage.getItem('accessToken');
  const refreshToken = localStorage.getItem('refreshToken');

  if (accessToken && refreshToken) {
    // Verify the token
    this.authService.verifyToken().subscribe({
      next: (response) => {
        console.log('User automatically logged in:', response);
        this.router.navigate(['profile']);
      },
      error: (error) => {
        console.error('Token verification failed:', error);
        this.clearTokens();
        this.router.navigate(['landing']);
      },
    });
  } else {
    // Check for tokens in cookies
    this.authService.refreshToken().subscribe({
      next: (response: any) => {
        // Store new tokens in localStorage
        localStorage.setItem('accessToken', response.accessToken);
        localStorage.setItem('refreshToken', response.refreshToken);
        this.router.navigate(['profile']);
      },
      error: (error) => {
        console.error('Token refresh failed:', error);
        this.clearTokens();
        this.router.navigate(['landing']);
      },
    });
  }
}

clearTokens() {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
}


}