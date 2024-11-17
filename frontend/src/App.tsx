import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import CourseTree, { CourseTreeProps } from './Tree'
import CourseTree2 from './CourseTree2'
import { Course } from './Course'
import Search from './Search'

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

  let [coursePath, setCoursePath] = useState([ps.courses.get("CPEN-211") ?? {
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
  }]);


  let graphNodeClicked = (course: string) => {
    let courseObj = ps.courses.get(course);
    console.log("Clicked: " + course);
    if (courseObj !== undefined) {
      setCoursePath((cp) => {
        let loc = cp.indexOf(courseObj);
        if (loc !== -1) {
          return cp.splice(0, loc+1);
        }
        return cp.concat([courseObj]);
      });
    }
  }

  return (
    <>
      <div className="w-screen h-screen">
        <CourseTree2 coursePath={coursePath} onClick={graphNodeClicked}></CourseTree2>
      </div>
      <div className="lg:hidden fixed top-0 left-0 right-0 m-5 bg-white shadow-md rounded p-3">
        <Search entered={(course)=>{console.log(course);}} />
      </div>
    </>
  )
}

export default App
