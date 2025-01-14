import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AuthService} from "../auth/auth.service";
import {BehaviorSubject, Observable, switchMap, take} from "rxjs";
import {LoginData} from "../../shared/models/loginData";
import {SignupData} from "../../shared/models/signupData";
import {Vote} from "../../shared/models/vote";
import {VoteType} from "../../shared/models/voteType";
import {QuestionFilter} from "../../shared/models/questionFilter";

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl: string = 'http://localhost:8080'

  navbarRep: BehaviorSubject<object> = new BehaviorSubject<object>({});

  constructor(private http: HttpClient, private authService: AuthService) {
  }


  login(data: LoginData): Observable<any> {
    return this.http.post<any>(this.apiUrl + '/user/login', data)
  }

  signup(data: SignupData): Observable<any> {
    return this.http.post(this.apiUrl + '/user/register', data)
  }

  getDashQuestions(startIndex: number): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    return this.http.get(this.apiUrl + '/question/questions?startIndex=' + startIndex + "&filter=" + QuestionFilter.Personalised, {headers});
  }

  getNewestQuestions(startIndex: number): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    return this.http.get(this.apiUrl + '/question/questions?startIndex=' + startIndex + "&filter=" + QuestionFilter.Newest, {headers});
  }

  getBountiedQuestions(startIndex: number): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    return this.http.get(this.apiUrl + '/question/questions?startIndex=' + startIndex + "&filter=" + QuestionFilter.Bountied, {headers});
  }

  getUnansweredQuestions(startIndex: number): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    return this.http.get(this.apiUrl + '/question/questions?startIndex=' + startIndex + "&filter=" + QuestionFilter.Unanswered, {headers});
  }

  getInbox(): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    return this.http.get(this.apiUrl + '/user/inbox', {headers});
  }

  markNotificationAs(id: string, read: boolean): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    return this.http.patch(this.apiUrl + '/notification?notificationId=' + id, read, {headers})
  }

  markAllAsRead() {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    return this.http.patch(this.apiUrl + '/notification/markAll', {}, {headers})

  }

  search(tags: string, searchTerm: string, startIndex: number): Observable<any> {
    return this.http.get(this.apiUrl + '/question/search?tagNames=' + tags + "&search=" + searchTerm + "&startIndex=" + startIndex);
  }


  getTags(startIndex: number): Observable<any> {
    return this.http.get(this.apiUrl + '/tag/all?startIndex=' + startIndex)
  }

  getSeparateQuestion(questionId: string): Observable<any> {
    return this.http.get(this.apiUrl + '/question?id=' + questionId);
  }

  tagsAutocomplete(substring: string, existingTags: any): Observable<any> {
    return this.http.post(this.apiUrl + '/tag/autocomplete?substring=' + substring, existingTags)
  }

  postNewQuestion(questionData: any): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    const url = this.apiUrl + '/question/create';
    return this.http.post<any>(url, questionData, {headers});
  }

  postNewAnswer(questionId: string, answerData: any): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    const url = this.apiUrl + '/answer/create?questionId=' + questionId;
    return this.http.post<any>(url, answerData, {headers});
  }

  getUserVotes(): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    return this.http.get(this.apiUrl + "/user/votes", {headers});
  }

  vote(vote: VoteType, answerId: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    return this.http.patch(this.apiUrl + '/answer/vote?answerId=' + answerId, {type: vote}, {headers});
  }

  voteQuestion(vote: VoteType, questionId: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    return this.http.patch(this.apiUrl + '/question/vote?questionId=' + questionId, {type: vote}, {headers});
  }

  logView(questionId: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    return this.http.patch(this.apiUrl + '/question/viewed?id=' + questionId, {}, {headers});
  }

  isOwner(questionId: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    return this.http.get(this.apiUrl + '/question/isOwner?questionId=' + questionId, {headers})
  }

  postBounty(questionId: string, value: number): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    return this.http.post(this.apiUrl + "/bounty/create?questionId=" + questionId, {value: value}, {headers})
  }

  accept(questionId: string, answerId: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    return this.http.patch(this.apiUrl + '/answer/accept?questionId=' + questionId + '&answerId=' + answerId, {}, {headers});
  }

  getTag(tagId: string, filter: QuestionFilter): Observable<any> {
    return this.http.get(this.apiUrl + '/tag?id=' + tagId + "&filter=" + filter);
  }

  navbarReputation(): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    this.http.get(this.apiUrl + '/user/navbar-reputation', {headers}).subscribe({
      next: (res: any) => {
        this.navbarRep.next({username: res.username, rep: res.reputationValueDTO});
      },
      error: () => {
        console.log("User is unauthorised");
      }
    });

    return this.navbarRep.asObservable();
  }

  resetReputation(): Observable<any> {
    this.navbarRep.next({});
    return this.navbarRep.asObservable();
  }


  calculatePostedAt(postedAt: any) {
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
    return `${days} day${days === 1 ? '' : 's'} ago`;
  };


}
