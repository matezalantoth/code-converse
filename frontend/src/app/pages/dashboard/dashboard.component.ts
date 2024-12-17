import {Component} from '@angular/core';
import {ApiService} from "../../services/data/api.service";
import {MainPageQuestion} from "../../shared/models/mainPageQuestion";
import {BehaviorSubject} from "rxjs";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  nextPage: number = 0;
  maxPage: number = 0;
  private _questions: BehaviorSubject<MainPageQuestion[]> = new BehaviorSubject<MainPageQuestion[]>([]);
  public questions = this._questions.asObservable();

  constructor(public api: ApiService) {
    api.getDashQuestions().subscribe(res => {
      this.nextPage = res.pagination.currentPage + 1;
      this.maxPage = res.pagination.maxPage;
      this._questions.next(res.questions);
    })
  }
}
