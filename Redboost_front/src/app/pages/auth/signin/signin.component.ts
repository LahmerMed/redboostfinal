import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { RippleModule } from 'primeng/ripple';
import { AuthService } from '../auth.service';
import { MessageService } from 'primeng/api';
import { environment } from '../../../../environment';

@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [
    ButtonModule,
    CheckboxModule,
    InputTextModule,
    PasswordModule,
    FormsModule,
    RouterModule,
    RippleModule,
  ],
  templateUrl: './signin.component.html', // Reference the external HTML file
  styleUrls: ['./signin.component.scss'],
})
export class SigninComponent {
  email: string = '';
  password: string = '';
  selectedRole: string = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService
  ) {}

 // signin.component.ts
onLogin() {
  console.log('Attempting login with email:', this.email);
  this.authService.login(this.email, this.password).subscribe({
    next: (response) => {
      console.log('Login successful:', response);
      const accessToken = response.accessToken;
      const refreshToken = response.refreshToken;

      if (accessToken) {
        // Store the tokens in localStorage
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
      }

      this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Login successful' });
      this.router.navigate(['profile']);
    },
    error: (error) => {
      console.error('Login failed:', error);
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Login failed' });
    },
  });
}

  // signin.component.ts
async onGoogleLogin() {
  if (!this.selectedRole) {
    alert('Please select a role before continuing.');
    return;
  }

  console.log('Firebase initialized with config:', environment.firebaseConfig);

  (await this.authService.googleLogin(this.selectedRole)).subscribe({
    next: (response: any) => {
      const accessToken = response.accessToken;
      const refreshToken = response.refreshToken;

      console.log('Google login successful:', response);
      if (accessToken) {
        // Store the tokens in localStorage
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
      }

      this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Google login successful' });
      this.router.navigate(['profile']);
    },
    error: (error: any) => {
      console.error('Google login failed:', error);
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Google login failed' });
    },
  });
}

  
}