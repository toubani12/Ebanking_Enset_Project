import { Routes } from '@angular/router';
import { athenticationGuard } from './guards/athentication-guard';
import { authorisationGuard } from './guards/authorisation-guard';

export const routes: Routes = [
    {path: 'login', loadComponent: () => import('./login/login').then(m => m.Login)},
    {path: '', redirectTo: '/login', pathMatch: 'full' },
    {path:"admin", loadComponent: () => import('./admin-template/admin-template').then(m => m.AdminTemplate), canActivate: [athenticationGuard], children:[ 
    {path:"customers", loadComponent: () => import('./customers/customers').then(m => m.Customers)},
    {path:"accounts", loadComponent: () => import('./accounts/accounts').then(m => m.Accounts)},
    {path:"new-customer", loadComponent: () => import('./new-custmer/new-custmer').then(m => m.NewCustmer), canActivate: [authorisationGuard]},
    {path:"not-authorised", loadComponent: () => import('./not-authorised/not-authorised').then(m => m.NotAuthorised)}
]},
   
    ]