import { Injectable } from '@angular/core';
import {AuthService} from "./auth.service";
import {NavigationService} from "../nav/nav.service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService {

  constructor(private authService: AuthService, private nav: NavigationService) {
  }

  canActivate(): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    }
    this.nav.redirectToLogin();
    return false;
  }

}
