import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from '../navbar/navbar';

@Component({
  selector: 'app-admin-template',
  imports: [RouterOutlet,Navbar],
  templateUrl: './admin-template.html',
  styleUrl: './admin-template.css',
})
export class AdminTemplate {}
