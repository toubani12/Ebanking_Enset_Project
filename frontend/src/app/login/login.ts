import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../services/Auth/auth-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {
  loginForm!: FormGroup;

  constructor(private fb: FormBuilder,private authService: AuthService,private router: Router) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  handleLogin(): void {
    if (this.loginForm.valid) {
      let username = this.loginForm.value.username;
      let password = this.loginForm.value.password;
      this.authService.login(username, password).subscribe({
        next: (response) => {
          this.authService.loadProfile(response);
          this.router.navigate(['/admin']);
        },
        error: (error) => {
          console.error('Login failed', error);
        }
      });
    }
  }
}
