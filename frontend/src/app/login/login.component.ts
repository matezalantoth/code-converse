import { Component } from '@angular/core';
import {LoginData} from "../shared/models/loginData";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../services/auth/auth.service";
import {NavigationService} from "../services/nav/nav.service";
import {ApiService} from "../services/data/api.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm: FormGroup;
  responseData: any;

  constructor(private authService: AuthService, public nav: NavigationService, private fb: FormBuilder, private api: ApiService) {
    this.loginForm = this.fb.group(
      {
        email: ['', [Validators.email, Validators.required]],
        password: ['', [Validators.minLength(6), Validators.required]]
      }
    )
  }

  onSubmit(event: Event) {
    event.preventDefault();

    if (this.loginForm.valid) {
      const data: any = this.loginForm.value;
      const loginData = {email: data.email, password: data.password};
      console.log(loginData);
      this.login(loginData);
    }
  }

  login(userData: LoginData) {
    this.api.login(userData).subscribe((res) => {
      if (!res.email) {
        console.log('Something went wrong...')
        return;
      }
      this.responseData = res;
      this.authService.setToken(this.responseData.token);
      console.log(res.token)
      this.nav.redirectToDashboard();
    })
  }
}
