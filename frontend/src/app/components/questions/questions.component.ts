import {Component, Input, OnInit} from '@angular/core';
import {ApiService} from "../../services/data/api.service";
import {NavigationService} from "../../services/nav/nav.service";
import {MainPageQuestion} from "../../shared/models/mainPageQuestion";
import {Observable} from "rxjs";

@Component({
  selector: 'app-questions',
  templateUrl: './questions.component.html',
  styleUrl: './questions.component.css'
})
export class QuestionsComponent implements OnInit {
  @Input()
  public _questions!: Observable<MainPageQuestion[]>
  public questions!: MainPageQuestion[]

  constructor(public api: ApiService, private nav: NavigationService) {
  }

  redirectToQuestion(id: string) {
    this.nav.redirectToQuestionPage(id);
  }

  ngOnInit() {
    this._questions.subscribe(q => {
      this.questions = q;
    });
  }


}
