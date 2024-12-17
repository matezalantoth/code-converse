import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./pages/login/login.component";
import {ProfileGuardService} from "./services/profile-guard/profile-guard.service";
import {SignupComponent} from "./pages/signup/signup.component";
import {DashboardComponent} from "./pages/dashboard/dashboard.component";
import {AskQuestionComponent} from "./pages/ask-question/ask-question.component";
import {AuthGuardService} from "./services/auth/auth-guard.service";
import {QuestionPageComponent} from "./pages/question-page/question-page.component";
import {QuestionsPageComponent} from "./pages/questions-page/questions-page.component";

const routes: Routes = [
  {path: '', component: DashboardComponent},
  {path: 'ask', component: AskQuestionComponent, canActivate: [AuthGuardService]},
  {path: 'login', component: LoginComponent, canActivate: [ProfileGuardService]},
  {path: 'signup', component: SignupComponent, canActivate: [ProfileGuardService]},
  {path: 'question', component: QuestionPageComponent},
  {path: 'questions', component: QuestionsPageComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {

}
