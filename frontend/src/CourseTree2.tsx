import { useEffect, useRef, useState } from 'react';
import { Course, GRADE_TO_COLOUR, Position } from './Course';
import cytoscape from "cytoscape";

export interface CourseTreeProps {
  // Oh boy here we go
  coursePath: Course[];
  onClick: (course: string) => void,
  courseCache: Map<string, Course>,
  reset: boolean
}

const COREQ_SPACING = 100;
const ROW_SPACING = 125;

enum EDGE_TYPE {
  PREREQ, COREQ, POSTREQ, NONE, PATH
}

// const NODE_COLOUR = "#5FB4D5";
const NODE_COLOUR = "#aaaaaa";
const DELECTED_COLOUR = "#002145";

const PREREQ_COLOUR = "#ccc";
const POSTREQ_COLOUR = "#ccc";
const COREQ_COLOUR = "#ccc";

/**
 * Convert the path to cytoscape nodes
 *
 * @param The CourseTreeProps, specifically the coursepath and the coursecache
 * @param oldCoursePos The positions of the nodes in the last redraw.
 *        It will be returned at the end of the function
 * 
 * @returns [Cytoscape json that defines the graph, the positions of all the elements this redraw]
 */
function makeElements(props: CourseTreeProps, oldCoursePos: Map<string, Position>) {

  if (props.coursePath.length < 1) {
    console.log("No course selected");
    return { elements: [], oldCoursePos: oldCoursePos };
  }

  let elements: {
    data: { id: string, label: string } | { source: string, target: string, label: string }, position?: { x: number, y: number }, style?: { 'line-color': string, 'target-arrow-color': string } | { 'background-color': string, 'font-family': string }
  }[] = [];

  let coursePos: Map<string, { x: number, y: number }> = new Map<string, { x: number, y: number }>();

  // The last element of the coursePath is the current course
  let currentCourse = props.coursePath[props.coursePath.length - 1];
  let cx = 0, cy = 0;

  // If there is an existing position for currentCourse sue that
  if (oldCoursePos.has(currentCourse.code)) {
    // Center it around the current position then
    cx = (oldCoursePos.get(currentCourse.code) ?? { x: 0, y: 0 }).x;
    cy = (oldCoursePos.get(currentCourse.code) ?? { x: 0, y: 0 }).y;
  }
  else {
    oldCoursePos.set(currentCourse.code, { x: cx, y: cy });
  }

  /**
   * Detects if the element is in the same place as any 
   * elements with permenant positions (ones on the path)
   *
   * @param pos A position
   * @returns true if there is already a node there
   */
  let isCollision = (pos: Position) => {
    for (let course of props.coursePath) {
      if (oldCoursePos.has(course.code)) {
        let oldPos = oldCoursePos.get(course.code) ?? { x: 0, y: 0 };
        if (oldPos.x == pos.x && oldPos.y == pos.y) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * If the course already has a permanent position, return that.
   * Otherwise, return what was put in
   * 
   * @param sampleCourse The name of the course
   * @param samplePos The position we where we're considering placing it
   * @returns An object. Changed is true if the object's position does not equal samplePos
   *          realPos: The actual position
   */
  let getPosition = (sampleCourse: string, samplePos: Position) => {
    if (sampleCourse == currentCourse.code) {
      return { changed: false, realPos: { x: cx, y: cx } };
    }

    // Check to see if it's a course in the coursePath
    for (let course of props.coursePath) {
      if (course.code != sampleCourse) {
        continue;
      }
      if (oldCoursePos.has(course.code)) {
        let oldPos = oldCoursePos.get(course.code) ?? { x: 0, y: 0 };

        return { changed: true, realPos: oldPos };
      }
    }

    return { changed: false, realPos: samplePos };
  }

  /**
   * Adds a course to the cytoscape json. If the course already exists but
   * the parent is different, it will just add an edge.
   * It also sets the colour of the course based on the grade
   * if it has an entry in the courseCache.
   *
   * @param course The course to add. If undefined the function does nothing
   * @param parent If not undefined, an edge specified by edge_type will be made 
   *               between this node and the parent.
   * @param pos Where to put the node
   * @param edge_type What kind of edge to add. Prereq adds an arrow from parent to course.
   *                  Postreq adds an arrow from this node to parent.
   *                  Coreq adds an arrow going both ways.
   * @param color The node colour. If this doesn't equal NODE_COLOUR it will override
   *              the colour from the grade.
   */
  let addCourse = (course: string | undefined, parent: string | undefined,
    pos: { x: number, y: number }, edge_type: EDGE_TYPE, 
    color: string = NODE_COLOUR) => {

    if (course == null) {
      return;
    }

    // Add the node if it's not there already
    if (!coursePos.has(course)) {
      let real_color = color;
      if (color !== DELECTED_COLOUR) {
        // Get the colour based on grade
        let courseInfo = props.courseCache.get(course);
        if (courseInfo !== undefined) {
          if (courseInfo.others.grade !== -1) {
            real_color = GRADE_TO_COLOUR(courseInfo.others.grade);
          }
        }
      }

      // Add the element
      // This always raises a warning because we're using the style property
      // But we have to to get dynamic colours
      // Womp womp
      elements.push({
        data: { id: course, label: course.replace('-', ' ') },
        position: pos,
        style: {
          'background-color': real_color,
          'font-family': "'Roboto', 'Arial', 'sans-serif'"
        }
      })

      coursePos.set(course, pos);
    }

    // Add the edge
    if (parent != undefined) {
      if (edge_type == EDGE_TYPE.PREREQ || edge_type == EDGE_TYPE.COREQ) {
        let col = edge_type == EDGE_TYPE.COREQ ? COREQ_COLOUR : PREREQ_COLOUR;
        elements.push({
          data: { source: course, target: parent, label: "" },
          style: { "line-color": col, "target-arrow-color": col }
        });
      }
      if (edge_type == EDGE_TYPE.POSTREQ || edge_type == EDGE_TYPE.COREQ) {
        let col = edge_type == EDGE_TYPE.COREQ ? COREQ_COLOUR : POSTREQ_COLOUR;
        elements.push({
          data: { source: parent, target: course, label: "" },
          style: { "line-color": col, "target-arrow-color": col }
        });
      }
    }
  };


  /**
   * Adds an array of courses in a row. 
   * Will not put a node where there is already a node.
   * All nodes will be connected to currentNode with an edge of edge_type.
   *
   * @param courses the courses to add
   * @param y the y value of the row
   * @param edge_type What type of edge to draw
   */
  let addRow = (courses: string[], y: number, edge_type: EDGE_TYPE) => {
    let startPos = cx - COREQ_SPACING * (Math.ceil(courses.length / 2))

    for (let course of courses) {
      // Bugs in the rest of the stack sometimes give me shit data
      // So just ignore it
      if (course == "") {
        continue;
      }

      // Calculate position
      let pos = { x: startPos, y: y };
      while (isCollision(pos)) {
        startPos += COREQ_SPACING;
        pos = { x: startPos, y: y };
      }

      let { changed, realPos } = getPosition(course, pos);
      addCourse(course, currentCourse.code, realPos, edge_type);

      if (!changed) {
        startPos += COREQ_SPACING;
      }
    }
  }

  // Add the rest of the path
  let lastElement = undefined;
  for (let course of props.coursePath) {
    if (oldCoursePos.has(course.code)) {
      let oldPos = oldCoursePos.get(course.code) ?? { x: 0, y: 0 };

      // Make sure it shows the same edgeType it used to show
      let edgeType = EDGE_TYPE.NONE;
      if (lastElement == undefined) {
        edgeType = EDGE_TYPE.NONE
      }
      else if (lastElement.prerequisites.indexOf(course.code) != -1) {
        edgeType = EDGE_TYPE.PREREQ;
      }
      else if (lastElement.postrequisites.indexOf(course.code) != -1) {
        edgeType = EDGE_TYPE.POSTREQ;
      }
      else if (lastElement.corequisites.indexOf(course.code) != -1) {
        edgeType = EDGE_TYPE.COREQ;
      }

      let x = undefined;
      if (lastElement != undefined) {
        x = lastElement.code;
        if (course.code == currentCourse.code) {
          if (course.prerequisites.indexOf(lastElement.code) != -1) {
            edgeType = EDGE_TYPE.NONE;
          }
          else if (course.postrequisites.indexOf(lastElement.code) != -1) {
            edgeType = EDGE_TYPE.NONE;
          }
          else if (course.corequisites.indexOf(lastElement.code) != -1) {
            edgeType = EDGE_TYPE.NONE;
          }
        }
      }
      addCourse(course.code, x, oldPos, edgeType, DELECTED_COLOUR);

      lastElement = course;
    }
    else {
      console.error("This should never happen");
    }
  }

  // Add everything else
  addRow(currentCourse.corequisites, cy, EDGE_TYPE.COREQ);
  addRow(currentCourse.prerequisites, cy - ROW_SPACING, EDGE_TYPE.PREREQ);
  addRow(currentCourse.postrequisites, cy + ROW_SPACING, EDGE_TYPE.POSTREQ);


  return { elements: elements, oldCoursePos: coursePos };
}


export default function CourseTree2(props: CourseTreeProps) {
  const containerRef = useRef(null);
  const cyRef = useRef<null | cytoscape.Core>(null);

  let [oldCoursePos, setOldCoursePos] = useState(new Map<string, Position>());

  let nodeClicked = (event: any) => {
    let node = event.target;

    props.onClick(node.id());
  };

  // When props are changed re-build the cytoscape
  useEffect(() => {
    if (containerRef.current !== null) {
      let newOldCoursePos = new Map<string, Position>();
      if (props.coursePath.length > 1) {
        newOldCoursePos = oldCoursePos;
      }

      // Re-build the elements
      let { elements, oldCoursePos: _oldCoursePos } = makeElements(props, newOldCoursePos);
      setOldCoursePos(_oldCoursePos);

      if (cyRef.current == null) {
        // Setup the cytoscape
        cyRef.current = cytoscape({
          container: containerRef.current,
          elements: elements,
          style: [
            {
              selector: 'node',
              style: {
                'label': 'data(label)'
              }
            },
            {
              selector: 'edge',
              style: {
                'width': 3,
                'line-color': '#ccc',
                'target-arrow-color': '#ccc',
                'target-arrow-shape': 'triangle',
                'curve-style': 'bezier'
              }
            }
          ],
          autolock: true,
          minZoom: 0.25,
          maxZoom: 2,
          wheelSensitivity: 0.6,
        });

        cyRef.current.fit();
        cyRef.current.on("tap", "node", nodeClicked);
      }
      else {
        // Add all the elements
        for (let el of elements) {
          cyRef.current.add(el);
        }

        // if (props.coursePath.length <= 1) {
        if (props.reset) {
          cyRef.current.fit();
        }
      }

      return () => {
        // To rebuild, remove all the elements
        for (let el of elements) {
          if (cyRef.current !== null) {
            if ((el.data as { id: string, label: string }).id !== undefined) {
              var j = cyRef.current.$("#" + (el.data as { id: string, label: string }).id);
              cyRef.current.remove(j);
            }
          }
        }
      }
    }
  }, [props]);

  return <div className="w-full h-screen" ref={containerRef} />
}
