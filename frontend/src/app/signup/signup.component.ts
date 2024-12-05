import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ApiService} from "../services/data/api.service";
import {AuthService} from "../services/auth/auth.service";
import {NavigationService} from "../services/nav/nav.service";
import {SignupData} from "../shared/models/signupData";
import {LoginData} from "../shared/models/loginData";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css'
})

export class SignupComponent {
  signupForm: FormGroup
  runningActions: any = {}

  constructor(private fb: FormBuilder, private apiService: ApiService, private authService: AuthService, public nav: NavigationService) {
    this.signupForm = this.fb.group({
      username: ['', [Validators.required]],
      email: ['', [Validators.required]],
      password: ['', [Validators.minLength(6), Validators.required]],
    })
  }

  onSubmit(event: Event) {
    if (this.runningActions.onSubmit) {
      return;
    }
    this.runningActions.onSubmit = true;
    event.preventDefault();
    if (this.signupForm.valid) {
      const signupData: SignupData = {...this.signupForm.value};
      this.apiService.signup(signupData).subscribe((res) => {
        if(res.jwt){
          this.authService.setToken(res.jwt);
          this.nav.redirectToDashboard();
        }
      })
    }
    this.runningActions.onSubmit = false;
  }

}
