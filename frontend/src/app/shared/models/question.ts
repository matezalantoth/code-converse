import {Answer} from "./answer";

export interface Question {
  id: string,
  title: string,
  content: string,
  posterName: string,
  postedAt: string,
  votes: number,
  answers: Answer[]
  hasAccepted: boolean,
  tags: any[]
}
