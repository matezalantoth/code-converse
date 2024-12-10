import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AuthService} from "../auth/auth.service";
import {Observable} from "rxjs";
import {LoginData} from "../../shared/models/loginData";
import {SignupData} from "../../shared/models/signupData";

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl: string = '/api'

  constructor(private http: HttpClient, private authService: AuthService) {
  }


  login(data: LoginData): Observable<any> {
    return this.http.post<any>(this.apiUrl + '/user/login', data)
  }

  signup(data: SignupData): Observable<any> {
    return this.http.post(this.apiUrl + '/user/register', data)
  }

  getDashQuestions(): Observable<any> {
    return this.http.post(this.apiUrl + '/question/main-questions', {"startIndex": 1})
  }

  getSeparateQuestion(questionId: string): Observable<any> {
    return this.http.get(this.apiUrl + '/question?id=' + questionId);
  }

  tagsAutocomplete(substring: string, existingTags: any): Observable<any>{
    return this.http.post(this.apiUrl + '/tag/autocomplete?substring=' + substring, existingTags)
  }

  postNewQuestion(questionData: any): Observable<any>{
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    const url = this.apiUrl + '/question/create';
    return this.http.post<any>(url, questionData, { headers });
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



}
