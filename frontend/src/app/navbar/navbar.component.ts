import { Component } from '@angular/core';
import {Observable} from "rxjs";
import {AuthService} from "../services/auth/auth.service";
import {NavigationService} from "../services/nav/nav.service";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  isLoggedIn: Observable<boolean>;

  constructor(private auth: AuthService, private nav: NavigationService) {
    this.isLoggedIn = this.auth.isUserLoggedIn()
  }

  logout() {
    this.auth.logout()
    this.nav.redirectToLogin()
  }
}
