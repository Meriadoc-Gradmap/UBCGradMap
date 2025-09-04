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

// The production build script changes this to dev_no
export const DEVELOPMENT = "dev_no";
//export let API_ENDPOINT = import.meta.env.BASE_URL;
export let API_ENDPOINT = "";

if (DEVELOPMENT.endsWith("yes")) {
  API_ENDPOINT = "http://localhost:8080/";
}

export let GRADE_TO_COLOUR = (grade: number) => {
  let hue = (Math.max(63, Math.min(grade, 90)) - 63) * (1 / 30) * 120;
  return "#" + convert.hsv.hex([hue, 55, 85]);
}
