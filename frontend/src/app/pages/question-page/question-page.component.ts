import { Component } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {NavigationService} from "../../services/nav/nav.service";
import {ApiService} from "../../services/data/api.service";
import {FormBuilder, FormGroup} from "@angular/forms";
import {AuthService} from "../../services/auth/auth.service";

@Component({
  selector: 'app-question-page',
  templateUrl: './question-page.component.html',
  styleUrl: './question-page.component.css'
})
export class QuestionPageComponent {

  public question: any;

  public showRequired: boolean;

  public answerForm: FormGroup;

  constructor(private route: ActivatedRoute, public api: ApiService ,private nav: NavigationService, private fb: FormBuilder, private auth: AuthService) {
    this.answerForm = this.fb.group({
      content: ['']
    });
    this.showRequired = false;
  }

  onSubmit(event: Event): void {
    this.auth.isUserLoggedIn().subscribe((res) => {
      if(res){
        event.preventDefault()
        if (this.answerForm.valid){
          this.api.postNewAnswer(this.question.id, this.answerForm.value).subscribe(res => {
            console.log(res);
            this.question.answers.push(res);
            console.log(this.question);
          })
          return;
        }
        this.showRequired = true;
        return;
      }
      this.nav.redirectToLogin();
    })
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if(params['questionId']){
        this.api.getSeparateQuestion(params['questionId']).subscribe(res => {
          this.question = res;
          console.log(res);
        });
        return;
      }
      this.nav.redirectToDashboard();
    })
  }
}
