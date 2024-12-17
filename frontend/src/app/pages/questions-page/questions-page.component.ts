import {Component, OnInit, OnDestroy} from '@angular/core';
import {MainPageQuestion} from "../../shared/models/mainPageQuestion";
import {ApiService} from "../../services/data/api.service";
import {QuestionFilter} from "../../shared/models/questionFilter";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";

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
export class QuestionsPageComponent implements OnInit, OnDestroy {
  nextPage: number = 0;
  maxPage: number = 0;
  questionsCount: number = 0;
  bountyCount: number = 0;
  private _questions: BehaviorSubject<MainPageQuestion[]>;
  public questions: Observable<MainPageQuestion[]>;
  selected: BehaviorSubject<QuestionFilter> = new BehaviorSubject<QuestionFilter>(QuestionFilter.Newest);
  private destroy$ = new Subject<void>();

  constructor(public api: ApiService) {
    this._questions = new BehaviorSubject<MainPageQuestion[]>([]);
    this.questions = this._questions.asObservable();
  }

  handleNewQuestions(res: QuestionResponse) {
    this.nextPage = res.pagination.currentPage + 1;
    this.maxPage = res.pagination.maxPage;
    this.questionsCount = res.count;
    this._questions.next(res.questions);
    this.bountyCount = res.bountyCount;
  }

  ngOnInit() {
    this.selected
      .pipe(takeUntil(this.destroy$))
      .subscribe((f) => {
        switch (f) {
          case QuestionFilter.Newest:
            this.api.getDashQuestions().subscribe((res) => this.handleNewQuestions(res));
            break;
          case QuestionFilter.Bountied:
            this.api.getBountiedQuestions().subscribe((res) => this.handleNewQuestions(res));
            break;
          case QuestionFilter.Unanswered:
            this.api.getUnansweredQuestions().subscribe((res) => this.handleNewQuestions(res));
            break;
        }
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly QuestionFilter = QuestionFilter;

  select(toSelect: QuestionFilter) {
    this.selected.next(toSelect);
  }
}
