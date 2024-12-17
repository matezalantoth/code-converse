import {Component} from '@angular/core';
import '@fortawesome/fontawesome-free/css/all.css';
import {BehaviorSubject, Observable} from "rxjs";
import {QuestionFilter} from "./shared/models/questionFilter";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'frontend';
}
