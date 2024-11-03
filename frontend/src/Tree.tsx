import React, { useLayoutEffect, useRef, useState } from 'react';
import CytoscapeComponent from 'react-cytoscapejs';
import { Course } from './Course';

export interface CourseTreeProps {
  // Oh boy here we go
  currentCourse: Course,
  courses: Map<string, Course>,
  onClick: (course: Course) => void,

}

const COREQ_SPACING = 100;
const ROW_SPACING = 100;

/**
 * Creates a course tree.
 */
export default function CourseTree(props: CourseTreeProps) {

  /*const elements = [
    { data: { id: 'one', label: 'Node 1' }, position: { x: 0, y: 0 } },
    { data: { id: 'two', label: 'Node 2' }, position: { x: 100, y: 0 } },
    { data: { source: 'one', target: 'two', label: 'Edge from Node1 to Node2' } }
  ];*/
  let elements: {
    data: { id: string, label: string } | { source: string, target: string, label: string }, position?: { x: number, y: number }
  }[] = [];

  const ref = useRef(null);
  const [width, setWidth] = useState(0);
  const [height, setHeight] = useState(0);

  useLayoutEffect(() => {
    setWidth(ref.current.offsetWidth);
    setHeight(ref.current.offsetHeight);
  }, []);

  const cx = width / 2;
  const cy = height / 2;

  let coursePos: Map<string, { x: number, y: number }> = new Map<string, { x: number, y: number }>();

  // Create the current course
  elements.push({
    data: { id: props.currentCourse.code, label: props.currentCourse.code },
    position: { x: cx, y: cy }
  });
  coursePos.set(props.currentCourse.code, { x: cx, y: cy });

  let addCourse = (course: Course | undefined, parent: string | undefined, pos: { x: number, y: number }) => {
    if (course == null) {
      return;
    }

    if (!coursePos.has(course.code)) {
      // Add the element
      elements.push({
        data: { id: course.code, label: course.code },
        position: pos,
      })
    }

    // Add the edge
    if (parent != null) {
      elements.push({ data: { source: course.code, target: parent, label: "" } });
    }
  };

  let startPos = cx - COREQ_SPACING * (Math.floor(props.currentCourse.corequisites.length / 2))

  for (let course of props.currentCourse.corequisites) {
    if (props.courses.has(course)) {
      addCourse(props.courses.get(course), props.currentCourse.code, { x: startPos, y: cy });

      startPos += COREQ_SPACING;
      if (startPos == cx) {
        startPos += COREQ_SPACING;
      }
    }
  }

  startPos = cx - COREQ_SPACING * (Math.floor(props.currentCourse.corequisites.length / 2))

  for (let course of props.currentCourse.prerequisites) {
    if (props.courses.has(course)) {
      addCourse(props.courses.get(course), props.currentCourse.code, { x: startPos, y: cy - ROW_SPACING });
      startPos += COREQ_SPACING;
    }
  }

  startPos = cx - COREQ_SPACING * (Math.floor(props.currentCourse.corequisites.length / 2))

  for (let course of props.currentCourse.postrequisites) {
    if (props.courses.has(course)) {
      addCourse(props.courses.get(course), props.currentCourse.code, { x: startPos, y: cy + ROW_SPACING });
      startPos += COREQ_SPACING;
    }
  }

  // Deal with the rest
  for (let course of props.courses.values()) {
    // TODO: later
  }

  let nodeClicked = (event: any) => {
    let node = event.target;

    let course = props.courses.get(node.id());
    if (course != null) {
      props.onClick(course);
    }
  };

  return <div className="w-full h-screen" ref={ref}>
    <CytoscapeComponent elements={elements} style={{ width: '100%', height: '100%' }}
      autolock={true} cy={(cy) => { cy.on("tap", "node", nodeClicked); }} />
  </div>;
}


