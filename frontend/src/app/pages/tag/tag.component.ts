import {Component, OnInit} from '@angular/core';
import {ApiService} from "../../services/data/api.service";
import {ActivatedRoute} from "@angular/router";
import {BehaviorSubject, Observable} from "rxjs";
import {QuestionFilter} from "../../shared/models/questionFilter";
import {Question} from "../../shared/models/question";
import {MainPageQuestion} from "../../shared/models/mainPageQuestion";

@Component({
  selector: 'app-tag',
  templateUrl: './tag.component.html',
  styleUrl: './tag.component.css'
})
export class TagComponent implements OnInit {
  private tagId!: string;
  public tag: any;
  nextPage: number = 0;
  maxPage: number = 0;
  private _questions: BehaviorSubject<MainPageQuestion[]> = new BehaviorSubject<MainPageQuestion[]>([]);
  public questions: Observable<MainPageQuestion[]> = this._questions.asObservable();
  questionsCount: number = 0;
  bountyCount: number = 0;
  private _selected: BehaviorSubject<QuestionFilter> = new BehaviorSubject<QuestionFilter>(QuestionFilter.Newest);
  public selected: Observable<QuestionFilter> = this._selected.asObservable();

  constructor(private api: ApiService, private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.selected.subscribe(filter => {
        this.tagId = params['tagId']
        this.api.getTag(params['tagId'], filter).subscribe(res => {
          this.tag = res;
          this.nextPage = res.data.pagination.currentPage + 1;
          this.maxPage = res.data.pagination.maxPage;
          this.questionsCount = res.data.count;
          this.bountyCount = res.data.bountyCount;
          this._questions.next(res.data.questions);
        });
      })

    })
  }

  select(toSelect: QuestionFilter) {
    this._selected.next(toSelect);
  }

  protected readonly QuestionFilter = QuestionFilter;
}
