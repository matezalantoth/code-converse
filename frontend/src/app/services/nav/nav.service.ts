import {Injectable} from '@angular/core';
import {Router} from "@angular/router";
import {ApiService} from "../data/api.service";
import {AuthService} from "../auth/auth.service";

@Injectable({
  providedIn: 'root'
})
export class NavigationService {


  constructor(private router: Router) {
  }

  redirectToSignup() {
    this.router.navigate(['/signup']);
  }

  redirectToLink(link: string) {
    window.location.href = "https://codeconverse.net" + link;
  }

  redirectToSearchResults() {
    this.router.navigate(['/search-results'])
  }

  redirectToLogin() {
    this.router.navigate(['/login']);
  }

  redirectToProfile() {
    this.router.navigate(['/profile']);
  }

  redirectToDashboard() {
    this.router.navigate(['/']);
  }

  redirectToTags() {
    this.router.navigate(['/tags'])
  }

  redirectToTag(tagId: string) {
    this.router.navigate(['/tag'], {queryParams: {tagId: tagId}})
  }

  redirectToQuestionPage(questionId: string) {
    this.router.navigate(['/question'], {queryParams: {questionId: questionId}})
  }

  redirectToQuestionsPage() {
    this.router.navigate(['/questions'])
  }

  redirectToAskQuestion() {

    this.router.navigate(['/ask']);
  }
}
