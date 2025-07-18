  import { Component } from '@angular/core';
  import { CommonModule } from '@angular/common';
  import { ReactiveFormsModule } from '@angular/forms';
  import { FormBuilder, FormGroup, Validators } from '@angular/forms';
  import { AuthService } from '../../services/auth';
  import { Router } from '@angular/router';

  @Component({
    selector: 'app-login',
    standalone: true,
    imports: [
      CommonModule,
      ReactiveFormsModule 
    ],
    templateUrl: './login.html',
    styleUrls: ['./login.css']
  })
  export class Login {
    loginForm: FormGroup;

    constructor(
      private fb: FormBuilder,
      private authService: AuthService,
      private router: Router
    ) {
      this.loginForm = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required]]
      });
    }
    onSubmit(): void {
      if (this.loginForm.invalid) {
        return;
      }
      const credentials = this.loginForm.value;

      this.authService.login(credentials)
        .subscribe({
          next: (response) => {
            console.log('Token:', response.token);
            localStorage.setItem('authToken', response.token);

            this.router.navigate(['/dashboard']);
          },
          error: (err) => {
            console.error('Erro no login:', err);
          }
        });
    }
  }