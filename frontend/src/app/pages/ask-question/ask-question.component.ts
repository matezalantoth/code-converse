import { Component } from '@angular/core';
import {AuthService} from "../../services/auth/auth.service";
import {NavigationService} from "../../services/nav/nav.service";
import {FormBuilder, FormGroup} from "@angular/forms";
import {ApiService} from "../../services/data/api.service";

@Component({
  selector: 'app-ask-question',
  templateUrl: './ask-question.component.html',
  styleUrl: './ask-question.component.css'
})
export class AskQuestionComponent {
  questionForm: FormGroup;
  tags: any[];
  tagResults: any[];
  showRequired: boolean;

  constructor(private authService: AuthService, public nav: NavigationService, private fb: FormBuilder, private api: ApiService) {
    this.questionForm = this.fb.group(
      {
        title: [''],
        content: ['']
      }
    )
    this.tags = [];
    this.tagResults = [];
    this.showRequired = false;

  }

  onSubmit(event: Event) {
    event.preventDefault();

    if (this.questionForm.valid) {
      const data: any = this.questionForm.value;
      const questionData = {title: data.title, content: data.content, tags: this.tags.map(t => t.tag)};
      console.log(questionData)
      this.postQuestion(questionData);
      return;
    }
    this.showRequired = true;
  }

  handleTagSelect(tag: any){
    this.tags.push(tag);
    this.tagResults = [];
  }

  getResults(event: Event){
    this.api.tagsAutocomplete((event.target as HTMLInputElement).value, this.tags).subscribe((res) => {
        this.tagResults = res;
    })
  }

  removeTag(tag: any){
    this.tags = this.tags.filter(t => t.tag.id != tag.tag.id);
  }

  postQuestion(questionData: any){
    this.api.postNewQuestion(questionData).subscribe((res) => {
        this.nav.redirectToDashboard();
    })
  }
}
