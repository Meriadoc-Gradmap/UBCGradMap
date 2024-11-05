import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import CourseTree, { CourseTreeProps } from './Tree'

function App() {

  let ps = {
    courses: new Map([
      ["CPEN-211", {
        code: "CPEN-211",
        name: "Soft constuction",
        description: "pretty cool",
        credits: [4, 5],
        prerequisites: ["APSC-160", "FINN-101"],
        corequisites: ["FINN-204", "FINN-206"],
        postrequisites: ["FINN-312", "FINN-401"],
        cdf: false,
        schedule: { lectures: 3, alternating1: false, labs: 2, alternating2: false, tutorials: 4, alternating3: true },
        others: { average: 1, professor: "Sathish the goat" }
      }],

      ["APSC-160", {
        code: "APSC-160",
        name: "Easy coding",
        description: "pretty cool",
        credits: [4, 5],
        prerequisites: [],
        corequisites: [],
        postrequisites: ["CPEN-211", "FINN-401"],
        cdf: false,
        schedule: { lectures: 3, alternating1: false, labs: 2, alternating2: false, tutorials: 4, alternating3: true },
        others: { average: 1, professor: "The grek" }
      }],

      ["FINN-101", {
        code: "FINN-101",
        name: "Finn 101",
        description: "pretty cool",
        credits: [4, 5],
        prerequisites: [],
        corequisites: [],
        postrequisites: ["CPEN-211"],
        cdf: false,
        schedule: { lectures: 3, alternating1: false, labs: 2, alternating2: false, tutorials: 4, alternating3: true },
        others: { average: 1, professor: "The grek" }
      }],

      ["FINN-204", {
        code: "FINN-204",
        name: "Finn 101",
        description: "pretty cool",
        credits: [4, 5],
        prerequisites: ["APSC-160"],
        corequisites: [],
        postrequisites: [],
        cdf: false,
        schedule: { lectures: 3, alternating1: false, labs: 2, alternating2: false, tutorials: 4, alternating3: true },
        others: { average: 1, professor: "The grek" }
      }],

      ["FINN-206", {
        code: "FINN-206",
        name: "Finn 101",
        description: "pretty cool",
        credits: [4, 5],
        prerequisites: [],
        corequisites: [],
        postrequisites: ["FINN-312"],
        cdf: false,
        schedule: { lectures: 3, alternating1: false, labs: 2, alternating2: false, tutorials: 4, alternating3: true },
        others: { average: 1, professor: "The grek" }
      }],

      ["FINN-312", {
        code: "FINN-312",
        name: "Finn 101",
        description: "pretty cool",
        credits: [4, 5],
        prerequisites: ["FINN-206", "CPEN-211"],
        corequisites: ["FINN-204"],
        postrequisites: ["FINN-401"],
        cdf: false,
        schedule: { lectures: 3, alternating1: false, labs: 2, alternating2: false, tutorials: 4, alternating3: true },
        others: { average: 1, professor: "The grek" }
      }],

      ["FINN-401", {
        code: "FINN-401",
        name: "Finn 101",
        description: "pretty cool",
        credits: [4, 5],
        prerequisites: ["FINN-206", "CPEN-211", "FINN-312"],
        corequisites: [],
        postrequisites: [],
        cdf: false,
        schedule: { lectures: 3, alternating1: false, labs: 2, alternating2: false, tutorials: 4, alternating3: true },
        others: { average: 1, professor: "The grek" }
      }],

    ])
  };

  let [currentCourse, setCurrentCourse] = useState(ps.courses.get("CPEN-211") ?? {
    code: "CPEN-211",
    name: "Soft constuction",
    description: "pretty cool",
    credits: [4, 5],
    prerequisites: ["APSC-160", "FINN-101"],
    corequisites: ["FINN-204", "FINN-206"],
    postrequisites: ["FINN-312", "FINN-401"],
    cdf: false,
    schedule: { lectures: 3, alternating1: false, labs: 2, alternating2: false, tutorials: 4, alternating3: true },
    others: { average: 1, professor: "Sathish the goat" }
  });

  return (
    <>
      <CourseTree currentCourse={currentCourse} courses={ps.courses} onClick={setCurrentCourse}></CourseTree>
    </>
  )
}

export default App
