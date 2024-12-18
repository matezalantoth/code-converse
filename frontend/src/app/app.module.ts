import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {LoginComponent} from './pages/login/login.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {provideHttpClient, withInterceptorsFromDi} from "@angular/common/http";
import {NavbarComponent} from './shared/navbar/navbar.component';
import {DashboardComponent} from './pages/dashboard/dashboard.component';
import {SignupComponent} from './pages/signup/signup.component';
import {AskQuestionComponent} from './pages/ask-question/ask-question.component';
import {QuestionPageComponent} from './pages/question-page/question-page.component';
import {SidebarComponent} from './shared/sidebar/sidebar.component';
import {AnswerComponent} from './pages/answer/answer.component';
import {BountyComponent} from './components/bounty/bounty.component';
import {provideToastr, ToastrModule, ToastrService} from "ngx-toastr";
import {BrowserAnimationsModule, NoopAnimationsModule} from "@angular/platform-browser/animations";
import {BountyAwarenessComponent} from './components/bounty-awareness/bounty-awareness.component';
import {NgOptimizedImage} from "@angular/common";
import {QuestionsComponent} from "./components/questions/questions.component";
import {QuestionsPageComponent} from './pages/questions-page/questions-page.component';
import {TagsComponent} from './pages/tags/tags.component';
import {TagComponent} from './pages/tag/tag.component';
import {MarkdownPreviewComponent} from './components/markdown-preview/markdown-preview.component';
import { InfiniteScrollComponent } from './components/infinite-scroll/infinite-scroll.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    NavbarComponent,
    DashboardComponent,
    SignupComponent,
    AskQuestionComponent,
    QuestionPageComponent,
    SidebarComponent,
    AnswerComponent,
    BountyComponent,
    BountyAwarenessComponent,
    QuestionsComponent,
    QuestionsPageComponent,
    TagsComponent,
    TagComponent,
    MarkdownPreviewComponent,
    InfiniteScrollComponent
  ],
  bootstrap: [AppComponent], imports: [BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule, NoopAnimationsModule, BrowserAnimationsModule, NgOptimizedImage, FormsModule],
  providers: [provideHttpClient(withInterceptorsFromDi()), provideToastr()]
})
export class AppModule {
}
