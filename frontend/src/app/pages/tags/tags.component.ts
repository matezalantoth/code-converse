import {Component, OnInit} from '@angular/core';
import {ApiService} from "../../services/data/api.service";
import {BehaviorSubject, Observable} from "rxjs";
import {MainPageQuestion} from "../../shared/models/mainPageQuestion";
import {NavigationService} from "../../services/nav/nav.service";

@Component({
  selector: 'app-tags',
  templateUrl: './tags.component.html',
  styleUrl: './tags.component.css'
})
export class TagsComponent implements OnInit {
  nextPage: number = 1;
  maxPage: number = 0;
  private _tags: BehaviorSubject<any[]>;
  public tags: Observable<any[]>;

  constructor(private api: ApiService, private nav: NavigationService) {
    this._tags = new BehaviorSubject<any[]>([]);
    this.tags = this._tags.asObservable();
  }

  ngOnInit() {
    this.api.getTags(this.nextPage).subscribe({
      next: (res) => {
        this.handleNewTags(res)
      },
      error: () => {
        console.log("something went wrong!");
      }
    });
  }

  redirectToTag(tagId: string) {
    this.nav.redirectToTag(tagId);
  }

  handleNewTags(res: any) {
    console.log(res.tags);
    this.nextPage = res.pagination.currentPage + 1;
    this.maxPage = res.pagination.maxPage;
    this._tags.next(res.tags);

  }


}
