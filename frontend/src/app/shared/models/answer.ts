export interface Answer {
  id: string,
  content: string,
  posterUsername: string,
  questionId: string,
  accepted: boolean,
  votes: number
}
