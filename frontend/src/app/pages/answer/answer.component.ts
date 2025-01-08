import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {Answer} from "../../shared/models/answer";
import {VoteType} from "../../shared/models/voteType";
import {Vote} from "../../shared/models/vote";
import {ApiService} from "../../services/data/api.service";
import {Question} from "../../shared/models/question";
import {Marked} from "marked";
import {markedHighlight} from "marked-highlight";
import hljs from "highlight.js";

@Component({
  selector: 'app-answer',
  templateUrl: './answer.component.html',
  styleUrls: ['./answer.component.css']
})
export class AnswerComponent implements OnChanges, OnInit {
  private currentVote: VoteType = VoteType.NOVOTE;

  @Input() public answer!: Answer;
  @Input() public userVotes!: Vote[];
  @Input() public owner!: boolean;
  @Input() public question!: Question;
  @Output() questionChange = new EventEmitter<any>();
  public content!: string | Promise<string>;

  constructor(private api: ApiService) {
  }

  private marked: Marked = new Marked(
    markedHighlight({
      emptyLangClass: 'hljs',
      langPrefix: 'hljs language-',
      highlight(code, lang, info) {

        const language = hljs.getLanguage(lang) ? lang : 'plaintext';
        return hljs.highlight(code, {language}).value;
      }
    }))

  ngOnInit() {
    this.markdownPreview();
  }


  markdownPreview() {
    console.log(this.answer);
    this.content = this.marked.parse(this.answer.content);
  }

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
        this.api.navbarReputation().subscribe();
        if (this.answer.accepted) {
          this.question.bounty = null;
        }
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
