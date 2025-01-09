import {Component, OnInit, OnDestroy} from '@angular/core';
import {MainPageQuestion} from "../../shared/models/mainPageQuestion";
import {ApiService} from "../../services/data/api.service";
import {QuestionFilter} from "../../shared/models/questionFilter";
import {BehaviorSubject, firstValueFrom, Observable, Subject} from "rxjs";
import {NavigationService} from "../../services/nav/nav.service";

interface QuestionResponse {
  pagination: {
    currentPage: number;
    maxPage: number;
  };
  count: number;
  questions: MainPageQuestion[];
  bountyCount: number;
}

@Component({
  selector: 'app-questions-page',
  templateUrl: './questions-page.component.html',
  styleUrls: ['./questions-page.component.css']
})
export class QuestionsPageComponent implements OnInit {
  nextPage: number = 1;
  maxPage: number = 1;
  questionsCount: number = 0;
  bountyCount: number = 0;
  private _questions: BehaviorSubject<MainPageQuestion[]>;
  public questions: Observable<MainPageQuestion[]>;
  selected: BehaviorSubject<QuestionFilter> = new BehaviorSubject<QuestionFilter>(localStorage.getItem("selected") ? localStorage.getItem("selected") as QuestionFilter : QuestionFilter.Newest);

  constructor(public api: ApiService, private nav: NavigationService) {
    this._questions = new BehaviorSubject<MainPageQuestion[]>([]);
    this.questions = this._questions.asObservable();
  }

  handleNewQuestions(res: QuestionResponse) {
    this.nextPage = res.pagination.currentPage + 1;
    this.maxPage = res.pagination.maxPage;
    this.questionsCount = res.count;
    const currentQuestions = this._questions.getValue();
    const updatedQuestions = [...currentQuestions, ...res.questions];
    this._questions.next(updatedQuestions);
    this.bountyCount = res.bountyCount;
    return this.maxPage >= this.nextPage;
  }

  redirectToAskQuestion() {
    this.nav.redirectToAskQuestion();
  }

  ngOnInit() {
    this.selected.subscribe(async (f) => {
      this.nextPage = 1;
      this.maxPage = 1;
      this._questions.next([]);
      localStorage.setItem("selected", this.selected.getValue());
      await this.fetchItems(f);
    })
  }

  fetchItems = async (f: QuestionFilter = this.selected.getValue()): Promise<boolean> => {
    switch (f) {
      case QuestionFilter.Newest:
        let newest = await firstValueFrom(this.api.getDashQuestions(this.nextPage));
        return this.handleNewQuestions(newest);
      case QuestionFilter.Bountied:
        let bountied = await firstValueFrom(this.api.getBountiedQuestions(this.nextPage));
        return this.handleNewQuestions(bountied);
      case QuestionFilter.Unanswered:
        let unanswered = await firstValueFrom(this.api.getUnansweredQuestions(this.nextPage));
        return this.handleNewQuestions(unanswered);
    }
  };

  protected readonly QuestionFilter = QuestionFilter;

  select(toSelect: QuestionFilter) {
    this.selected.next(toSelect);
  }
}
