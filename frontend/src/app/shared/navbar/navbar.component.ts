import {Component, ElementRef, Injectable, OnDestroy, OnInit, Renderer2, ViewChild} from '@angular/core';
import {Observable} from "rxjs";
import {AuthService} from "../../services/auth/auth.service";
import {NavigationService} from "../../services/nav/nav.service";
import {ApiService} from "../../services/data/api.service";
import {ThemeService} from "../../services/theme.service";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit, OnDestroy {
  isLoggedIn: Observable<boolean>;
  navbarRep: any
  inbox!: any[]
  showInbox: boolean = false;
  hover: boolean = false;
  private intervalId: any;
  private documentClickListener!: () => void;

  constructor(private nav: NavigationService, private api: ApiService, private auth: AuthService, private renderer: Renderer2, public theme: ThemeService) {
    this.isLoggedIn = this.auth.isUserLoggedIn();
    this.isLoggedIn.subscribe(() => {
      this.getInboxContents()
    })
    this.api.navbarReputation().subscribe(res => {
      this.navbarRep = res
    });
  }

  login() {
    this.nav.redirectToLogin()
  }

  signup() {
    this.nav.redirectToSignup()
  }

  redirectToProfile() {
    this.nav.redirectToProfile();
  }

  redirectToDashboard() {
    this.nav.redirectToDashboard()
  }

  getInbox() {
    if (!this.showInbox) {
      this.getInboxContents()
    }
    this.showInbox = !this.showInbox;
  }

  getInboxContents() {
    this.api.getInbox().subscribe(res => {
      this.inbox = res;
      this.orderInboxByDate();
    });
  }

  ngOnInit() {
    this.intervalId = setInterval(() => {
      this.getInboxContents();
    }, 600000);
    this.documentClickListener = this.renderer.listen('document', 'click', (event: MouseEvent) => {
      const target = event.target as HTMLElement;
      const inboxElements = Array.from(document.getElementsByClassName("inbox"));
      const inInbox = inboxElements.some(el => el.contains(target));
      if (!inInbox) {
        this.showInbox = false;
      }
    });
  }

  getUnreadCount() {
    return this.inbox.filter(val => !val.read).length;
  }


  orderInboxByDate() {
    // @ts-ignore
    this.inbox.sort((a, b) => new Date(b.sentAt) - new Date(a.sentAt));
  }


  ngOnDestroy() {
    if (this.documentClickListener) {
      this.documentClickListener();
    }
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  protected readonly localStorage = localStorage;
}
