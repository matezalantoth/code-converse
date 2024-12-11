import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { Answer } from "../../shared/models/answer";
import { VoteType } from "../../shared/models/voteType";
import { Vote } from "../../shared/models/vote";
import { ApiService } from "../../services/data/api.service";

@Component({
  selector: 'app-answer',
  templateUrl: './answer.component.html',
  styleUrls: ['./answer.component.css']
})
export class AnswerComponent implements OnChanges {
  private currentVote: VoteType = VoteType.NOVOTE;

  public displayedVotes: number = 0;

  @Input() public answer!: Answer;
  @Input() public userVotes!: Vote[];

  constructor(private api: ApiService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['userVotes'] && changes['userVotes'].currentValue) {
      this.checkAlreadyVoted(this.answer?.id);
    }
    this.displayedVotes = this.answer?.votes;
  }

  downvote(): void {
    if (this.currentVote === VoteType.DOWNVOTE) {
      this.resetVote();
    } else {
      this.applyVote(VoteType.DOWNVOTE, -1);
    }
  }

  upvote(): void {
    if (this.currentVote === VoteType.UPVOTE) {
      this.resetVote();
    } else {
      this.applyVote(VoteType.UPVOTE, 1);
    }
  }

  isVote(vote: VoteType): boolean {
    return vote === this.currentVote;
  }

  private checkAlreadyVoted(answerId: string): void {
    const existingVote = this.userVotes.find(v => v.answerId === answerId);
    this.currentVote = existingVote ? existingVote.voteType : VoteType.NOVOTE;
  }

  private resetVote(): void {
    const adjustment = this.currentVote === VoteType.UPVOTE ? -1 : 1;
    this.displayedVotes += adjustment;
    this.api.vote(this.currentVote, this.answer.id).subscribe({
      next: () => {this.currentVote = VoteType.NOVOTE;console.log("Vote reset successful")},
      error: (err) => console.error("Error resetting vote:", err),
    });
  }

  private applyVote(voteType: VoteType, adjustment: number): void {
    if (this.currentVote === VoteType.UPVOTE || this.currentVote === VoteType.DOWNVOTE) {
      const undoAdjustment = this.currentVote === VoteType.UPVOTE ? -1 : 1;
      this.displayedVotes += undoAdjustment;
    }
    this.displayedVotes += adjustment;
    this.currentVote = voteType;

    this.api.vote(voteType, this.answer.id).subscribe({
      next: () => console.log(`${voteType} successful`),
      error: (err) => {
        console.error("Error applying vote:", err);
        this.displayedVotes -= adjustment;
        this.currentVote = VoteType.NOVOTE;
      }
    });
  }

  protected readonly VoteType = VoteType;
}
