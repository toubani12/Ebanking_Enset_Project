import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http:  HttpClient) {
    this.loadToken();
  }
  isAuthenticated :boolean = false;
  roles : any;
  uaername:any;
  accesTokken! : String;

  loadToken() {
    if (typeof window !== 'undefined') {
      const token = window.localStorage.getItem('jwt-token');
      if (token) {
        this.accesTokken = token;
        let decodedjwt = atob(token.split('.')[1]);
        let jwtData = JSON.parse(decodedjwt);
        this.roles = jwtData.scope;
        this.uaername = jwtData.sub;
        this.isAuthenticated = true;
      }
    }
  }

  login(username: string, password: string) {
    let options = {
      headers : new HttpHeaders({
        'Content-Type': 'application/x-www-form-urlencoded'
      })
    }
    
    let params = new URLSearchParams();
    params.set('username', username);
    params.set('password', password);

    return this.http.post('http://localhost:8080/auth/login', params.toString(), options);
  }
loadProfile(data:any){
    this.accesTokken = data['access-token'];
    let decodedjwt = atob(this.accesTokken.split('.')[1]);
    let jwtData = JSON.parse(decodedjwt);
  this.roles = jwtData.scope;
  this.uaername = jwtData.sub;
  this.isAuthenticated = true;
  if (typeof window !== 'undefined') {
    window.localStorage.setItem('jwt-token', this.accesTokken as string);
  }

}

  logout() {
    this.isAuthenticated = false;
    this.accesTokken = '';
    this.roles = null;
    this.uaername = null;
    if (typeof window !== 'undefined') {
      window.localStorage.removeItem('jwt-token');
    }
  }
  
}
