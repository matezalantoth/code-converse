import {Answer} from "./answer";
import {Bounty} from "./bounty";

export interface Question {
  id: string,
  title: string,
  content: string,
  posterName: string,
  postedAt: string,
  votes: number,
  answers: Answer[]
  hasAccepted: boolean,
  tags: any[],
  posterRep: number,
  posterTrueRep: number
  bounty: Bounty | null
  
}
