import {Component, OnInit} from '@angular/core';
import {NavigationService} from "../../services/nav/nav.service";
import {ApiService} from "../../services/data/api.service";
import {BehaviorSubject, firstValueFrom, Observable} from "rxjs";
import {MainPageQuestion} from "../../shared/models/mainPageQuestion";
import {QuestionFilter} from "../../shared/models/questionFilter";

@Component({
  selector: 'app-search-results',
  templateUrl: './search-results.component.html',
  styleUrl: './search-results.component.css'
})
export class SearchResultsComponent implements OnInit {

  public searchResults: any;
  private nextPage = 1;
  private maxPage = 1;
  public questionsCount: number = 0;
  private _questions: BehaviorSubject<MainPageQuestion[]> = new BehaviorSubject<MainPageQuestion[]>([]);
  public questions = this._questions.asObservable();


  constructor(private nav: NavigationService, private api: ApiService) {
  }

  ngOnInit() {
    this.searchResults = JSON.parse(localStorage.getItem('searchJSON')!);
    this.nextPage = JSON.parse(localStorage.getItem('searchJSON')!).startIndex;
    this.fetchItems();
  }

  fetchItems = async (sort: boolean = true): Promise<boolean> => {
    const res = await firstValueFrom(this.api.search(this.searchResults.tagNames, this.searchResults.content, this.nextPage));
    this.nextPage = res.pagination.currentPage + 1;
    this.maxPage = res.pagination.maxPage;
    this.questionsCount = res.count;

    const currentQuestions = this._questions.getValue();
    let updatedQuestions = [...currentQuestions, ...res.questions];
    if (sort) {
      updatedQuestions = updatedQuestions.sort((a, b) => b.resultsScore - a.resultsScore);
    }
    this._questions.next(updatedQuestions);
    return this.maxPage >= this.nextPage;
  }

  redirectToAskQuestion() {
    this.nav.redirectToAskQuestion();
  }

  protected readonly QuestionFilter = QuestionFilter;
}
