import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router} from "@angular/router";
import {NavigationService} from "../../services/nav/nav.service";

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {

  show: boolean = false;

  constructor(private router: Router, private nav: NavigationService) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.show = (event as NavigationEnd).url !== '/login' && (event as NavigationEnd).url !== '/signup';
      }
    })
  }


  onPage(page: string): boolean {
    const currentPage = this.router.url
    if (page === '/') {
      return currentPage === page;
    }
    return currentPage.includes(page);
  }

  goHome() {
    this.nav.redirectToDashboard();
  }

  goQuestions() {
    this.nav.redirectToQuestionsPage();
  }

  goTags() {
    this.nav.redirectToTags();
  }
}
