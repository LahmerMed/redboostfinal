// confirm-email.component.ts
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirm-email',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './confirm-email.component.html',
  styleUrls: ['./confirm-email.css'],
})
export class ConfirmEmailComponent {
  confirmForm: FormGroup;
  email: string = '';

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.confirmForm = this.fb.group({
      code: ['', Validators.required],
    });

    // Get the email from the query parameters
    this.route.queryParams.subscribe((params) => {
      this.email = params['email'];
    });
  }

  onSubmit() {
    if (this.confirmForm.invalid) {
      return;
    }

    const confirmationRequest = {
      email: this.email,
      code: this.confirmForm.value.code,
    };

    this.http.post('http://localhost:8085/Auth/confirm-email', confirmationRequest).subscribe(
      () => {
        alert('Email confirmed successfully!');
        this.router.navigate(['/dashboard']); // Navigate to login page after confirmation
      },
      (error) => {
        alert('Invalid confirmation code. Please try again.');
      }
    );
  }
}