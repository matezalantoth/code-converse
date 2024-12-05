import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {ProfileGuardService} from "./services/profile-guard/profile-guard.service";
import {SignupComponent} from "./signup/signup.component";
import {DashboardComponent} from "./dashboard/dashboard.component";
import {AskQuestionComponent} from "./ask-question/ask-question.component";
import {AuthGuardService} from "./services/auth/auth-guard.service";

const routes: Routes = [
  {path: '', component: DashboardComponent},
  {path: 'ask', component: AskQuestionComponent, canActivate: [AuthGuardService]},
  {path: 'login', component: LoginComponent, canActivate: [ProfileGuardService]},
  {path: 'signup', component: SignupComponent, canActivate: [ProfileGuardService]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {

}
