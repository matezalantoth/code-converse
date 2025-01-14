import {Bounty} from "./bounty";

export interface MainPageQuestion {
  id: string,
  title: string,
  content: string,
  posterName: string,
  posterRep: number,
  postedAt: string,
  votes: number,
  answerCount: number
  hasAccepted: boolean,
  tags: any[],
  views: number,
  resultsScore: number,
  bounty: Bounty | null
}
