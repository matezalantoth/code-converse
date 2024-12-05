import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {ProfileGuardService} from "./services/profile-guard/profile-guard.service";

const routes: Routes = [
  {path: 'login', component: LoginComponent, canActivate: [ProfileGuardService]},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {

}
