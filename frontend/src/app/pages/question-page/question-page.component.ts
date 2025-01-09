import {Component, Input, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {NavigationService} from "../../services/nav/nav.service";
import {ApiService} from "../../services/data/api.service";
import {FormBuilder, FormGroup} from "@angular/forms";
import {AuthService} from "../../services/auth/auth.service";
import {Question} from "../../shared/models/question";
import {Vote} from "../../shared/models/vote";
import {VoteType} from "../../shared/models/voteType";
import {Marked} from "marked";
import {markedHighlight} from "marked-highlight";
import hljs from "highlight.js";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-question-page',
  templateUrl: './question-page.component.html',
  styleUrl: './question-page.component.css'
})

export class QuestionPageComponent implements OnInit {

  @Input()
  protected question!: Question;

  protected showRequired: boolean;

  private currentVote: VoteType = VoteType.NOVOTE;

  protected userVotes!: Vote[];

  protected owner!: boolean;

  protected answerForm: FormGroup;

  public content!: any;

  constructor(private route: ActivatedRoute, public api: ApiService, private toast: ToastrService, private nav: NavigationService, private fb: FormBuilder, private auth: AuthService) {
    this.answerForm = this.fb.group({
      content: ['']
    });
    this.showRequired = false;
  }

  onQuestionChange(updatedQuestion: any): void {
    this.question = updatedQuestion;
    this.sortAnswers();
  }

  sortAnswers(): void {
    if (this.question?.answers) {
      this.question.answers = [...this.question.answers.sort((a: any, b: any) => b.votes - a.votes)];
    }
  }

  isVote(vote: VoteType): boolean {
    return vote === this.currentVote;
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

  private applyVote(voteType: VoteType, adjustment: number): void {
    if (this.currentVote === VoteType.UPVOTE || this.currentVote === VoteType.DOWNVOTE) {
      const undoAdjustment = this.currentVote === VoteType.UPVOTE ? -1 : 1;
      this.question.votes += undoAdjustment;
    }
    this.question.votes += adjustment;
    this.currentVote = voteType;

    this.api.voteQuestion(voteType, this.question.id).subscribe({
      next: () => {

      },
      error: (err) => {
        if (this.owner) {
          this.toast.error("you can't vote on your own question", undefined, {
            positionClass: "toast-custom-top-center",
            timeOut: 1500
          })
          this.question.votes -= adjustment;
          this.currentVote = VoteType.NOVOTE;
          return;
        }
        this.nav.redirectToLogin();
      }
    });
  }

  private resetVote(): void {
    const adjustment = this.currentVote === VoteType.UPVOTE ? -1 : 1;
    this.question.votes += adjustment;
    this.api.voteQuestion(this.currentVote, this.question.id).subscribe({
      next: () => {
        this.currentVote = VoteType.NOVOTE;
      },
      error: (err) => {
        console.error("Error resetting vote:", err);
        this.question.votes -= adjustment;
      },
    });
  }

  onSubmit(event: Event): void {
    this.auth.isUserLoggedIn().subscribe((res) => {
      if (res) {
        event.preventDefault()
        if (this.answerForm.valid) {
          this.api.postNewAnswer(this.question.id, this.answerForm.value).subscribe(res => {
            this.question.answers.push(res);
            this.api.navbarReputation().subscribe();
          })
          return;
        }
        this.showRequired = true;
        return;
      }
      this.nav.redirectToLogin();
    })
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


  markdownPreview() {

    this.content = this.marked.parse(this.question.content);
  }

  ngOnInit(): void {

    this.route.queryParams.subscribe(params => {
      if (params['questionId']) {
        this.api.logView(params['questionId']).subscribe();
        this.api.getSeparateQuestion(params['questionId']).subscribe({
          next: (res) => {
            this.question = res;
            console.log(this.question)
            this.sortAnswers();
            this.markdownPreview();
          },
          error: () => {
            this.nav.redirectToDashboard();
          }

        });
        this.api.getUserVotes().subscribe({
          next: (res) => {
            this.userVotes = res.votes;
            this.checkAlreadyVoted(params['questionId']);
          },
          error: (err) => {
            console.error(err);
          }

        });
        this.api.isOwner(params['questionId']).subscribe(res => {
          this.owner = res;
        })
        return;
      }
      this.nav.redirectToDashboard();
    })
  }

  private checkAlreadyVoted(id: string): void {
    const existingVote = this.userVotes.find(v => v.id === id);
    this.currentVote = existingVote ? existingVote.voteType : VoteType.NOVOTE;
  }

  protected readonly VoteType = VoteType;
}
