import {Injectable} from '@angular/core';
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class NavigationService {

  constructor(private router: Router) {
  }

  redirectToSignup() {
    this.router.navigate(['/signup']);
  }

  redirectToLogin() {
    this.router.navigate(['/login']);
  }

  redirectToDashboard() {
    this.router.navigate(['/']);
  }
}
