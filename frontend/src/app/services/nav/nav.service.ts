import {Injectable} from '@angular/core';
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class NavigationService {

  constructor(private router: Router) {
  }

  redirectToSignup() {
    this.router.navigate(['/signup']);
  }

  redirectToLogin() {
    this.router.navigate(['/login']);
  }

  redirectToDashboard() {
    this.router.navigate(['/']);
  }

  redirectToTags(){
    this.router.navigate(['/tags'])
  }

  redirectToQuestionPage(questionId: string){
    this.router.navigate(['/question'], {queryParams: {questionId: questionId}}).then(() => {
       console.log(this.router.url)})
  }

  redirectToAskQuestion() {

    this.router.navigate(['/ask']);
  }
}
