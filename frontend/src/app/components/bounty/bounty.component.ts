import {Component, Input} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastrService} from "ngx-toastr";
import {Question} from "../../shared/models/question";
import {ApiService} from "../../services/data/api.service";

@Component({
  selector: 'app-bounty',
  templateUrl: './bounty.component.html',
  styleUrl: './bounty.component.css'
})
export class BountyComponent {

  public showForm: boolean

  @Input()
  public question!: Question;

  bountyForm: FormGroup;

  constructor(private fb: FormBuilder,  private toast: ToastrService, private api: ApiService) {
    this.showForm = false;
    this.bountyForm = this.fb.group(
      {
        value: ['', [Validators.min(10), Validators.max(1000)]]
      }
    )
  }

  onAddBountyClick(event: Event){
    if(!this.showForm) {
      this.showForm = true;
      return;
    }
    if(!this.bountyForm.valid){
      this.toast.error(this.bountyForm.value.value < 10 ? "Bounty must exceed 10 reputation" : "Bounty must subceed 1000 reputation", undefined, {
        positionClass: "toast-custom-top-center",
        timeOut: 1500
      });
      return;
    }
    if(this.bountyForm.value.value <= this.question.posterRep){
      this.showForm = false;
      this.api.postBounty(this.question.id, this.bountyForm.value.value).subscribe({
        next: (res) => {
          this.toast.success("Created bounty of " + res.bounty.value, undefined, {
            positionClass: "toast-custom-top-center",
            timeOut: 1500
          })
        },
        error: () => {

        }
      })

      return;
    }
    this.toast.info("You (" + this.question.posterRep + ") cannot afford a " + this.bountyForm.value.value + "rep bounty", undefined, {
      positionClass: "toast-custom-top-center",
      timeOut: 2000
    } )
  }

  hideForm(){
    this.showForm = false;
  }
}
