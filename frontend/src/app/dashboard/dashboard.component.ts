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

  calculatePostedAt(postedAt: any){
    const now = new Date();
    const tempPostedAt = new Date(postedAt);
    let offset = tempPostedAt.getTimezoneOffset() * -1;
    const postedDate = new Date(tempPostedAt.getTime() + offset * 60000);
    // @ts-ignore
    let differenceInSeconds = Math.floor(((now - postedDate) / 1000));
    if (differenceInSeconds < 60) {
      return `${differenceInSeconds} second${differenceInSeconds === 1 ? '' : 's'} ago`;
    } else if (differenceInSeconds < 3600) {
      const minutes = Math.floor(differenceInSeconds / 60);
      return `${minutes} minute${minutes === 1 ? '' : 's'} ago`;
    } else if (differenceInSeconds < 86400) {
      const hours = Math.floor(differenceInSeconds / 3600);
      return `${hours} hour${hours === 1 ? '' : 's'} ago`;
    }
    const days = Math.floor(differenceInSeconds / 86400);
    return `${days} days ago`;
  };

  redirectToAskQuestion(){
    this.nav.redirectToAskQuestion();
  }
}
