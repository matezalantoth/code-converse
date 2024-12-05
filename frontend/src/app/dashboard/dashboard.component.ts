import { Component } from '@angular/core';
import {ApiService} from "../services/data/api.service";
import {NavigationService} from "../services/nav/nav.service";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  nextPage: number = 0;
  maxPage: number = 0;
  questions: any;

  constructor(private api: ApiService, private nav: NavigationService) {
    api.getDashQuestions().subscribe(res => {
       this.nextPage = res.currentPage + 1;
       this.maxPage = res.maxPage;
       this.questions = res.questions;
       console.log(this.questions);
    })
  }

  redirectToAskQuestion(){
    this.nav.redirectToAskQuestion();
  }
}
