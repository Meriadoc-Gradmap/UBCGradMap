import { useEffect, useState } from 'react'
import './App.css'
import CourseTree2 from './CourseTree2'
import { API_ENDPOINT, Course } from './Course'
import Search from './Search'
import Panel from './Panel'
import Logo from './Logo'
import DarkSwitch from './DarkSwitch'

/**
  * Javascript's indexOf can't only search one attribute so here we are
  * This method searches for a course with the same code and returns it's index
  *
  * @param courses An array of courses to search
  * @param course The course to search for
  * @returns Either the index in the array of course, or -1 if it could not be found
  */
function courseIndexOf(courses: Course[], course: Course): number {
  let index = 0;

  for (let c of courses) {
    if (c.code === course.code) {
      return index;
    }
    index++;
  }

  return -1;
}

function App() {

  let [courseCache, setCourseCache] = useState(new Map<string, Course>());

  /**
   * Loads a course from the server or from cache
   *
   * @param code The course code to fetch
   * @returns A promise of either a Course object if the course exists, 
   *          or (null | undefined) if the course does not exist
   */
  let fetchCourse = async (code: string) => {

    if (code == "") {
      // Invalid course code
      return null;
    }

    // If it's in cache, just return that
    if (courseCache.has(code)) {
      return courseCache.get(code);
    }

    let data = await fetch(API_ENDPOINT + "api/getcourse?course=" + code);
    if (data.ok) {
      let course: string | Course = await data.json();

      if (!(typeof course === 'string')) {
        // Add it to cache
        // // We have to use the function because this is happening async-ly
        setCourseCache((old) => {
          let ncc = new Map(old);
          ncc.set(code, course as Course);
          return ncc;
        });
        return course;
      }
      else {
        // Course does not exist
        console.error("Server error: " + course);
        return null;
      }
    }
    else {
      console.error("Error: Could not connect to server.");
    }

    return null;
  }

  /**
   * Fetches the course, returns it, then fetches all of the course's
   * prerequesits, corequisites, and postrequisites in the background.
   *
   * @param code A course code
   * @returns A promise of either a Course object or (null | undefined) if it doesn't work
   */
  let superFetchCourse = async (code: string) => {

    let course = await fetchCourse(code);

    if (course == null || course == undefined) {
      // Typically this means the course is not offered
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

  /**
   * Fetch a course and set it as the only currently selected course
   *
   * @param code the course code to set
   */
  let loadCourse = async (code: string) => {

    let course = await superFetchCourse(code);

    if (course !== null) {
      setCoursePath([course]);
      setReset(true);
    }

  }

  let [coursePath, setCoursePath] = useState<Course[]>([]);

  // On start load CPEN 221 by default
  useEffect(() => {
    if (coursePath.length == 0) {
      loadCourse("CPEN-221");
    }
  }, []);

  let graphNodeClicked = async (course: string) => {
    let courseObj = await superFetchCourse(course);

    if (courseObj !== null && courseObj !== undefined) {
      setReset(false);
      setCoursePath((cp) => {
        // If it's a course we've already clicked on, revert the path to that point
        let loc = courseIndexOf(cp, courseObj);
        if (loc !== -1) {
          return cp.slice(0, loc + 1);
        }
        return cp.concat([courseObj]);
      });
    }
  }

  let [reset, setReset] = useState(true);

  return (
    <>
      <div className="w-screen h-screen">
        <CourseTree2 courseCache={courseCache} coursePath={coursePath} onClick={graphNodeClicked}
          reset={reset}></CourseTree2>
      </div>
      <Panel currentCourse={coursePath.length > 0 ? coursePath[coursePath.length - 1] : undefined} />
      <Search entered={(course) => {
        loadCourse(course);
      }} />
      <DarkSwitch />
      <Logo />
    </>
  )
}

export default App
