import { useEffect, useState } from 'react'
import './App.css'
import CourseTree2 from './CourseTree2'
import { API_ENDPOINT, Course } from './Course'
import Search from './Search'
import Panel from './Panel'
import Logo from './Logo'

function courseIndexOf(courses: Course[], course: Course): number {
  let index = 0;

  for (let c of courses) {
    if (c.code === course.code) {
      console.log("Found idx");
      return index;
    }
    index++;
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

    let data = await fetch(API_ENDPOINT + "api/getcourse?course=" + code);
    if (data.ok) {
      let course: string | Course = await data.json();

      if (!(typeof course === 'string')) {
        setCourseCache((old) => {
          let ncc = new Map(old);
          ncc.set(code, course as Course);
          return ncc;
        });
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

  let superFetchCourse = async (code: string) => {

    let course = await fetchCourse(code);

    if (course == null || course == undefined) {
      alert("Course not offered currently");
      return null;
    }

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

    return course;
  }

  let loadCourse = async (code: string) => {

    let course = await superFetchCourse(code);

    if (course !== null) {
      setCoursePath([course]);
    }

  }

  let [coursePath, setCoursePath] = useState<Course[]>([]);

  useEffect(() => {
    if (coursePath.length == 0) {
      loadCourse("CPEN-221");
    }
  }, []);

  let graphNodeClicked = async (course: string) => {
    let courseObj = await superFetchCourse(course);
    console.log("Clicked: " + course);
    if (courseObj !== null && courseObj !== undefined) {
      setCoursePath((cp) => {
        let loc = courseIndexOf(cp, courseObj);
        if (loc !== -1) {
          return cp.slice(0, loc + 1);
        }
        return cp.concat([courseObj]);
      });
    }
  }

  return (
    <>
      <div className="w-screen h-screen">
        <CourseTree2 courseCache={courseCache} coursePath={coursePath} onClick={graphNodeClicked}></CourseTree2>
      </div>
      <Panel currentCourse={coursePath.length > 0 ? coursePath[coursePath.length - 1] : undefined} />
      <Search entered={(course) => { loadCourse(course) }} />
      <Logo />
    </>
  )
}

export default App
