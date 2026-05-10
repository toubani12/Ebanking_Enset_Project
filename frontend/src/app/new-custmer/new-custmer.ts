import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CustomerService } from '../services/Customer/customers';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-new-custmer',
  imports: [ReactiveFormsModule,CommonModule],
  templateUrl: './new-custmer.html',
  styleUrl: './new-custmer.css',
})
export class NewCustmer implements OnInit {

  newCustmerFormGroup!: FormGroup;
  constructor(private router: Router,private formBuilder: FormBuilder,private customerService: CustomerService){}
  ngOnInit(): void {
    this.newCustmerFormGroup = this.formBuilder.group({
      name : this.formBuilder.control(null,Validators.required),
      email : this.formBuilder.control(null,Validators.required,),
    });
  }
  handleSaveCustomer() {
    const customer = this.newCustmerFormGroup.value;
    this.customerService.saveCustomer(customer).subscribe({
      next: (data) => {
        alert("customer saved successfully");
        this.newCustmerFormGroup.reset();
        this.newCustmerFormGroup.reset();
        this.router.navigate(['/customers']);

      },
      error: (err) => {
        alert(err);
      }
    });
  }
}
