import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';
import { MessageService } from 'primeng/api';
import { FormsModule } from '@angular/forms'; // Add this import
import { environment } from '../../../../environment';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule, // Add FormsModule here
  ],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss'],
})
export class SignUpComponent {
  registerForm: FormGroup;
  submitted = signal(false);
  errorMessage = signal('');
  selectedRole: string = ''; // Declare the selectedRole property

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private authService: AuthService,
    private messageService: MessageService
  ) {
    this.registerForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      phoneNumber: ['', Validators.required],
      role: ['', Validators.required],
    });
  }

  get f() {
    return this.registerForm.controls;
  }

  onSubmit() {
    this.submitted.set(true);
    if (this.registerForm.invalid) {
      return;
    }

    this.http.post('http://localhost:8085/Auth/register', this.registerForm.value).subscribe(
      (response: any) => {
        this.router.navigate(['/confirm-email'], { queryParams: { email: this.registerForm.value.email } });
      },
      (error) => {
        this.errorMessage.set('Registration failed. Please try again.');
      }
    );
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
        // Store the tokens in localStorage for quick access
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