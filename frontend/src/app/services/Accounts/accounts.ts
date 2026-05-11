import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AccountOperation {
  id: number;
  operationDate: Date;
  amount: number;
  type: string;
  description: string;
}

export interface AccountDetails {
  accountId: string;
  balance: number;
  currentPage: number;
  totalPages: number;
  pageSize: number;
  accountOperationDtos: AccountOperation[];
}

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  constructor(private http: HttpClient) {}

  public getAccount(accountId: string, page: number, size: number): Observable<AccountDetails> {
    return this.http.get<AccountDetails>("http://localhost:8080/accounts/"+accountId+"/pageOperations?page="+page+"&size="+size);
  }

  public debit(accountId: string, amount: number, description: string): Observable<any> {
    const params = new URLSearchParams();
    params.set('accountId', accountId);
    params.set('amount', amount.toString());
    params.set('description', description);
    return this.http.post("http://localhost:8080/accounts/debit?"+params.toString(), {});
  }

  public credit(accountId: string, amount: number, description: string): Observable<any> {
    const params = new URLSearchParams();
    params.set('accountId', accountId);
    params.set('amount', amount.toString());
    params.set('description', description);
    return this.http.post("http://localhost:8080/accounts/credit?"+params.toString(), {});
  }

  public transfer(accountSource: string, accountDestination: string, amount: number, description: string): Observable<any> {
    const params = new URLSearchParams();
    params.set('accountSource', accountSource);
    params.set('accountDestination', accountDestination);
    params.set('amount', amount.toString());
    return this.http.post("http://localhost:8080/accounts/transfer?"+params.toString(), {});
  }
}
