import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth/auth.service";
import {Observable} from "rxjs";
import {NavigationService} from "../../services/nav/nav.service";
import {ApiService} from "../../services/data/api.service";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  isLoggedIn: Observable<boolean>

  constructor(private auth: AuthService, private nav: NavigationService, private api: ApiService) {
    this.isLoggedIn = this.auth.isUserLoggedIn();
  }

  ngOnInit() {
    if (!this.isLoggedIn) {
      this.nav.redirectToDashboard();
    }
  }

  logout() {
    this.auth.logout()
    this.api.resetReputation().subscribe()
    this.nav.redirectToLogin()
  }

}
