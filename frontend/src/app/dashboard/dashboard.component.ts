import { Component } from '@angular/core';
import {ApiService} from "../services/data/api.service";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {

  constructor(private api: ApiService) {
  }
}
