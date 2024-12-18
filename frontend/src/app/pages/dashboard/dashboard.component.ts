import {Component} from '@angular/core';
import {ApiService} from "../../services/data/api.service";
import {MainPageQuestion} from "../../shared/models/mainPageQuestion";
import {BehaviorSubject, firstValueFrom} from "rxjs";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  nextPage: number = 1;
  maxPage: number = 1;
  private _questions: BehaviorSubject<MainPageQuestion[]> = new BehaviorSubject<MainPageQuestion[]>([]);
  public questions = this._questions.asObservable();

  constructor(public api: ApiService) {
    this.fetchItems();
  }

  fetchItems = async (): Promise<boolean> => {
    const res = await firstValueFrom(this.api.getDashQuestions(this.nextPage));
    this.nextPage = res.pagination.currentPage + 1;
    this.maxPage = res.pagination.maxPage;
    const currentQuestions = this._questions.getValue();
    const updatedQuestions = [...currentQuestions, ...res.questions];
    this._questions.next(updatedQuestions);
    return this.maxPage >= this.nextPage;

  }

}
