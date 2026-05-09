import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Customer } from '../../model/customer.model';

@Injectable({
  providedIn: 'root',
})
export class CustomerService{
  constructor(private http:HttpClient) {}
  customers: any;
  apiUrl = 'http://localhost:8080/';
  getCustomers() : Observable<Array<Customer>> {
    return this.http.get<Array<Customer>>(`${this.apiUrl}custmers`);
  }
  searchCustomers(keyword: string) : Observable<Array<Customer>> {
    return this.http.get<Array<Customer>>(`${this.apiUrl}custmers/search?keyword=${keyword}`);
  }
  saveCustomer(customer: Customer) : Observable<Customer> {
    return this.http.post<Customer>(`${this.apiUrl}custmers`,customer);
  }
  deleteCustomer(id: number) : Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}custmers/${id}`);
  }
}
