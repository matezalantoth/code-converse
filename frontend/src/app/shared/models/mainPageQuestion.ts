import {Bounty} from "./bounty";

export interface MainPageQuestion {
  id: string,
  title: string,
  content: string,
  posterName: string,
  postedAt: string,
  votes: number,
  answerCount: number
  hasAccepted: boolean,
  tags: any[],
  views: number,
  bounty: Bounty | null
}
