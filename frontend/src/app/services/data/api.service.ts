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
    return this.http.get(this.apiUrl + '/question/main-questions')
  }


}
