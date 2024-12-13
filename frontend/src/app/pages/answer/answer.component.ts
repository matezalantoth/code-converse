import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Answer } from "../../shared/models/answer";
import { VoteType } from "../../shared/models/voteType";
import { Vote } from "../../shared/models/vote";
import { ApiService } from "../../services/data/api.service";
import { Question } from "../../shared/models/question";

@Component({
  selector: 'app-answer',
  templateUrl: './answer.component.html',
  styleUrls: ['./answer.component.css']
})
export class AnswerComponent implements OnChanges {
  private currentVote: VoteType = VoteType.NOVOTE;

  @Input() public answer!: Answer;
  @Input() public userVotes!: Vote[];
  @Input() public owner!: boolean;
  @Input() public question!: Question;
  @Output() questionChange = new EventEmitter<any>();

  constructor(private api: ApiService) {}

  updateQuestion(): void {
    this.question.answers = [...this.question.answers.map(a => {
      return a.id === this.answer.id ? this.answer : a;
    })];
    this.questionChange.emit(this.question);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['userVotes'] && changes['userVotes'].currentValue) {
      this.checkAlreadyVoted(this.answer?.id);
    }
  }

  downvote(): void {
    if (this.currentVote === VoteType.DOWNVOTE) {
      this.resetVote();
    } else {
      this.applyVote(VoteType.DOWNVOTE, -1);
    }
    this.updateQuestion();
  }

  upvote(): void {
    if (this.currentVote === VoteType.UPVOTE) {
      this.resetVote();
    } else {
      this.applyVote(VoteType.UPVOTE, 1);
    }
    this.updateQuestion();
  }

  isVote(vote: VoteType): boolean {
    return vote === this.currentVote;
  }

  accept(): void {

    this.api.accept(this.question.id, this.answer.id).subscribe({
      next: (res) => {
        this.question.hasAccepted = !this.question.hasAccepted;
        this.answer.accepted = !this.answer.accepted;
      },
      error: (err) => {
        console.error("something went wrong accepting answer: " + err);
      }
    });
  }

  private checkAlreadyVoted(answerId: string): void {
    const existingVote = this.userVotes.find(v => v.id === answerId);
    this.currentVote = existingVote ? existingVote.voteType : VoteType.NOVOTE;
  }

  private resetVote(): void {
    const adjustment = this.currentVote === VoteType.UPVOTE ? -1 : 1;
    this.answer.votes += adjustment;
    this.api.vote(this.currentVote, this.answer.id).subscribe({
      next: () => {
        this.currentVote = VoteType.NOVOTE;
        console.log("Vote reset successful");
      },
      error: (err) => {
        console.error("Error resetting vote:", err);
        this.answer.votes -= adjustment;
      },
    });
  }

  private applyVote(voteType: VoteType, adjustment: number): void {
    if (this.currentVote === VoteType.UPVOTE || this.currentVote === VoteType.DOWNVOTE) {
      const undoAdjustment = this.currentVote === VoteType.UPVOTE ? -1 : 1;
      this.answer.votes += undoAdjustment;
    }
    this.answer.votes += adjustment;
    this.currentVote = voteType;

    this.api.vote(voteType, this.answer.id).subscribe({
      next: () => console.log(`${voteType} successful`),
      error: (err) => {
        console.error("Error applying vote:", err);
        this.answer.votes -= adjustment;
        this.currentVote = VoteType.NOVOTE;
      }
    });
  }

  protected readonly VoteType = VoteType;
}
