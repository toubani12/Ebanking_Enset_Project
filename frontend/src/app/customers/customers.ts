import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CustomerService } from '../services/Customer/customers';
import { BehaviorSubject, catchError, finalize, Observable, throwError } from 'rxjs';
import { Customer } from '../model/customer.model';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../services/Auth/auth-service';

@Component({
  selector: 'app-customers',
  imports: [CommonModule, ReactiveFormsModule],
  standalone: true,
  templateUrl: './customers.html',
  styleUrl: './customers.css',
})
export class Customers implements OnInit {
  private customersSubject = new BehaviorSubject<Array<Customer>>([]);
  customers$: Observable<Array<Customer>> = this.customersSubject.asObservable();

  errorMessage: any = null;
  loading = false;
  searchFormGroup!: FormGroup;

  constructor(
    private customerService: CustomerService,
    private formBuilder: FormBuilder,
    public authService: AuthService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    this.searchFormGroup = this.formBuilder.group({
      keyword: this.formBuilder.control('')
    });

    if (isPlatformBrowser(this.platformId)) {
      this.loadCustomers();
    }
  }

  loadCustomers(): void {
    this.loading = true;
    this.errorMessage = null;

    this.customerService.getCustomers().pipe(
      catchError((error) => {
        this.errorMessage = error.message || "An error occurred";
        return throwError(() => error);
      }),
      finalize(() => {
        this.loading = false;
      })
    ).subscribe({
      next: (customers) => {
        this.customersSubject.next(customers);
      },
      error: (err) => {
        console.error("Error loading customers", err);
      }
    });
  }

  handleSearchCustomer(): void {
    const keyword = this.searchFormGroup.get('keyword')?.value;

    if (!keyword || keyword.trim() === '') {
      this.loadCustomers();
      return;
    }

    this.loading = true;
    this.errorMessage = null;

    this.customerService.searchCustomers(keyword).pipe(
      catchError((error) => {
        this.errorMessage = error.message || "An error occurred";
        return throwError(() => error);
      }),
      finalize(() => {
        this.loading = false;
      })
    ).subscribe({
      next: (customers) => {
        this.customersSubject.next(customers);
      },
      error: (err) => {
        console.error("Error searching customers", err);
      }
    });
  }

  handleDeleteCustomer(id: number): void {
    if (confirm('Are you sure you want to delete this customer?')) {
      this.customerService.deleteCustomer(id).subscribe({
        next: () => {
          const currentCustomers = this.customersSubject.value;

          const updatedCustomers = currentCustomers.filter(
            customer => customer.id !== id
          );

          this.customersSubject.next(updatedCustomers);
        },
        error: (err) => {
          alert(err?.message || err);
        }
      });
    }
  }
}