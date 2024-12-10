import { Component } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {NavigationService} from "../../services/nav/nav.service";
import {ApiService} from "../../services/data/api.service";

@Component({
  selector: 'app-question-page',
  templateUrl: './question-page.component.html',
  styleUrl: './question-page.component.css'
})
export class QuestionPageComponent {

  public question: any;

  constructor(private route: ActivatedRoute, public api: ApiService ,private nav: NavigationService) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if(params['questionId']){
        this.api.getSeparateQuestion(params['questionId']).subscribe(res => {
          this.question = res;
          console.log(res);
        });
        return;
      }
      this.nav.redirectToDashboard();
    })
  }
}
