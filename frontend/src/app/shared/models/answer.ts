export interface Answer {
  id: string,
  content: string,
  posterUsername: string,
  posterReputation: number,
  questionId: string,
  accepted: boolean,
  votes: number,
  postedAt: string
}
