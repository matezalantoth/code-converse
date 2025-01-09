import {Component, Input} from '@angular/core';
import {NavigationService} from "../../services/nav/nav.service";
import {ApiService} from "../../services/data/api.service";

@Component({
  selector: 'app-inbox',
  templateUrl: './inbox.component.html',
  styleUrl: './inbox.component.css'
})
export class InboxComponent {
  @Input()
  public inbox: any;
  showOption: boolean = false;

  constructor(private nav: NavigationService, private api: ApiService) {
  }

  formatDateTime(isoString: string): string {
    const date = new Date(isoString);

    const months = [
      'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
      'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
    ];

    const month = months[date.getMonth()];
    const day = date.getDate();
    const year = date.getFullYear();

    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');

    return `${month} ${day}, ${year} at ${hours}:${minutes}`;
  }

  redirectToLink(n: any, link: string) {
    n.read = true;
    this.api.markNotificationAs(n.id, true).subscribe(() => {
      this.nav.redirectToLink(link);
    })

  }

  markAsRead(n: any, event: Event) {
    event.stopPropagation()
    this.api.markNotificationAs(n.id, true).subscribe(() => {
      n.read = true;
    });

  }

  markAsUnread(n: any, event: Event) {
    event.stopPropagation()
    this.api.markNotificationAs(n.id, false).subscribe(() => {
      n.read = false;
    });
  }

  markAllAsRead() {

    this.api.markAllAsRead().subscribe(() => {
      this.inbox = this.inbox.map((n: any) => {
        n.read = true;
        return n
      });
    });
  }


}
