export interface Course {
  code: string,
  name: string,
  description: string,
  credits: number[],
  prerequisites: string[],
  postrequisites: string[],
  corequisites: string[],
  cdf: boolean,
  schedule: Schedule,
  others: {
    average: number,
    professor: string
  }
}

export interface Schedule {
  lectures: number,
  alternating1: boolean,
  labs: number,
  alternating2: boolean,
  tutorials: number,
  alternating3: boolean
}

export interface Position {
  x: number,
  y: number
}

export const API_ENDPOINT = "http://localhost:8080"
