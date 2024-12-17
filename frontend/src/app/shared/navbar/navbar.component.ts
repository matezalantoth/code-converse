import {Component, Injectable, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {AuthService} from "../../services/auth/auth.service";
import {NavigationService} from "../../services/nav/nav.service";
import {ApiService} from "../../services/data/api.service";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  isLoggedIn: Observable<boolean>;
  navbarRep: any

  constructor(private nav: NavigationService, private api: ApiService, private auth: AuthService) {
    this.isLoggedIn = this.auth.isUserLoggedIn();
    this.api.navbarReputation().subscribe(res => {
      this.navbarRep = res
    });
  }

  logout() {
    this.auth.logout()
    this.nav.redirectToLogin()
  }

  redirectToAskQuestion() {
    this.nav.redirectToAskQuestion();
  }

  redirectToDashboard() {
    this.nav.redirectToDashboard()
  }


  protected readonly localStorage = localStorage;
}
