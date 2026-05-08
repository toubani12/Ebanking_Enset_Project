import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AccountDetails, AccountService } from '../services/Accounts/accounts';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/Auth/auth-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './accounts.html',
  styleUrl: './accounts.css',
})
export class Accounts implements OnInit {
  searchFormGroup!: FormGroup;
  operationFromGroup!: FormGroup;
  accountDetails!: AccountDetails;
  errorMessage!: string;
  currentPage: number = 0;
  pageSize: number = 5;
  pages: Array<number> = [];

  constructor(
    private fb: FormBuilder, 
    private accountService: AccountService,
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.searchFormGroup = this.fb.group({
      accountId: this.fb.control('', Validators.required)
    });
    this.operationFromGroup = this.fb.group({
      operationType: this.fb.control('DEBIT', Validators.required),
      amount: this.fb.control(0, [Validators.required, Validators.min(0.1)]),
      description: this.fb.control('', Validators.required),
      accountDestination: this.fb.control('')
    });
  }

  private handleOperationError(err: any) {
    console.error(err);
    if (err.status === 401 || err.status === 403) {
      this.authService.logout();
      this.router.navigateByUrl('/login');
    }
  }

  onSearchSubmit() {
    this.currentPage = 0;
    this.handleSearchAccount();
  }

  handleSearchAccount() {
    let accountId: string = this.searchFormGroup.value.accountId;
    this.accountService.getAccount(accountId, this.currentPage, this.pageSize).subscribe({
      next: (data) => {
        this.accountDetails = data;
        this.errorMessage = '';
        this.pages = new Array(data.totalPages);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || err.message;
        if (err.status === 401 || err.status === 403) {
            this.authService.logout();
            this.router.navigateByUrl('/login');
        }
        console.error(err);
      }
    });
  }

  gotoPage(event: Event, page: number) {
    event.preventDefault(); // Prevents default anchor click behavior
    this.currentPage = page;
    this.handleSearchAccount();
  }

  handleAccountOperation() {
    let accountId: string = this.searchFormGroup.value.accountId;
    let operationType = this.operationFromGroup.value.operationType;
    let amount: number = this.operationFromGroup.value.amount;
    let description: string = this.operationFromGroup.value.description;
    let accountDestination: string = this.operationFromGroup.value.accountDestination;

    if (operationType === 'DEBIT') {
      this.accountService.debit(accountId, amount, description).subscribe({
        next: () => {
          alert('Success Debit');
          this.operationFromGroup.reset({ operationType: 'DEBIT', amount: 0, description: '' });
          this.handleSearchAccount();
        },
        error: (err) => this.handleOperationError(err)
      });
    } else if (operationType === 'CREDIT') {
      this.accountService.credit(accountId, amount, description).subscribe({
        next: () => {
          alert('Success Credit');
          this.operationFromGroup.reset({ operationType: 'DEBIT', amount: 0, description: '' });
          this.handleSearchAccount();
        },
        error: (err) => this.handleOperationError(err)
      });
    } else if (operationType === 'TRANSFER') {
      this.accountService.transfer(accountId, accountDestination, amount, description).subscribe({
        next: () => {
          alert('Success Transfer');
          this.operationFromGroup.reset({ operationType: 'DEBIT', amount: 0, description: '' });
          this.handleSearchAccount();
        },
        error: (err) => this.handleOperationError(err)
      });
    }
  }
}
