import { Injectable } from '@angular/core';
import {AuthService} from "../auth/auth.service";
import {NavigationService} from "../nav/nav.service";


@Injectable({
  providedIn: 'root'
})
export class ProfileGuardService {

  constructor(private authService: AuthService, private nav: NavigationService) {
  }

  canActivate() {
    if (!this.authService.isAuthenticated()) {
      return true;
    }
    this.nav.redirectToDashboard();
    return false;
  }
}
