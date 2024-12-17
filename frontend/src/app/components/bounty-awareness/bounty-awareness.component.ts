import {Component, Input} from '@angular/core';
import {Bounty} from "../../shared/models/bounty";

@Component({
  selector: 'app-bounty-awareness',
  templateUrl: './bounty-awareness.component.html',
  styleUrl: './bounty-awareness.component.css'
})
export class BountyAwarenessComponent {

  @Input()
  public bounty!: Bounty

  public timeUntilExpiration(): string {
    const timeDifferenceInMillis = new Date(this.bounty.expiresAt).getTime() - new Date().getTime();

    const hoursUntilExpiration = timeDifferenceInMillis / (1000 * 60 * 60);
    const minutesUntilExpiration = timeDifferenceInMillis / (1000 * 60);

    if (hoursUntilExpiration >= 1) {
      return `${Math.floor(hoursUntilExpiration)} hour${Math.floor(hoursUntilExpiration) > 1 ? 's' : ''} remaining`;
    } else {
      return `${Math.floor(minutesUntilExpiration)} minute${Math.floor(minutesUntilExpiration) > 1 ? 's' : ''} remaining`;
    }
  }

}
