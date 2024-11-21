import convert from "color-convert";

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
    grade: number,
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

export let GRADE_TO_COLOUR = (grade: number) => {
  let hue = (Math.max(60, Math.min(grade, 90)) - 60)*(1/30) * 120; 
  return "#" + convert.hsv.hex([hue, 95, 80]);
}
