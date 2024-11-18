import { useEffect, useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import CourseTree, { CourseTreeProps } from './Tree'
import CourseTree2 from './CourseTree2'
import { API_ENDPOINT, Course } from './Course'
import Search from './Search'

function courseIndexOf(courses: Course[], course: Course): number {
  let index = 0;

  for (let c of courses) {
    if (c.code === course.code) {
      console.log("FOund idx");
      return index;
    }
    index ++;
  }

  return -1;
}

function App() {

  let [courseCache, setCourseCache] = useState(new Map<string, Course>());

  let fetchCourse = async (code: string) => {

    if (code == "") {
      return null;
    }

    if (courseCache.has(code)) {
      return courseCache.get(code);
    }
    
    let data = await fetch(API_ENDPOINT+"/api/getcourse?course=" + code);
    if (data.ok) {
      let course: string | Course = await data.json();

      if (!(typeof course === 'string')) {
        let newCourseCache = new Map(courseCache);
        newCourseCache.set(code, course as Course);
        setCourseCache(newCourseCache);
        return course;
      }
      else {
        console.error("Server error: " + course);
        return null;
      }
    }
    else {
      console.error("Error: Could not connect to server.");
    }

    return null;
  }

  let loadCourse = async (code: string) => {
    let course = await fetchCourse(code);

    if (course == null || course == undefined) {
      alert("Error: Could not load course");
      return;
    }

    setCoursePath([course]);

    // Start lazy-loading all the courses the user could click on soon
    for (let c of course.prerequisites) {
      fetchCourse(c);
    }

    for (let c of course.corequisites) {
      fetchCourse(c);
    }

    for (let c of course.postrequisites) {
      fetchCourse(c);
    }
  }

  let [coursePath, setCoursePath] = useState<Course[]>([]);

  // Initlally load CPEN 221
  useEffect(() => {
    loadCourse("CPEN-221");
  }, []);

  let graphNodeClicked = async (course: string) => {
    let courseObj = await fetchCourse(course);
    console.log("Clicked: " + course);
    if (courseObj !== null && courseObj !== undefined) {
      setCoursePath((cp) => {
        let loc = courseIndexOf(cp, courseObj);
        if (loc !== -1) {
          return cp.slice(0, loc+1);
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
      <Search entered={(course)=>{loadCourse(course)}} />
    </>
  )
}

export default App
