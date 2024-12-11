import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {NavigationService} from "../../services/nav/nav.service";

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {

  constructor(private router: Router, private nav: NavigationService) {}


  onPage(page: string): boolean {
    const currentPage = this.router.url
    if(page==='/'){
      return currentPage===page;
    }
    return currentPage.includes(page);
  }

  goHome(){
    this.nav.redirectToDashboard();
  }

  goTags(){
    this.nav.redirectToTags();
  }
}
