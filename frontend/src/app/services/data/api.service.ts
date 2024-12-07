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

  tagsAutocomplete(substring: string, existingTags: any): Observable<any>{
    return this.http.post(this.apiUrl + '/tag/autocomplete?substring=' + substring, existingTags)
  }

  postNewQuestion(questionData: any): Observable<any>{
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);
    const url = this.apiUrl + '/question/create';
    return this.http.post<any>(url, questionData, { headers });
  }


}
