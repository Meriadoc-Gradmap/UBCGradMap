import React, { useEffect, useLayoutEffect, useRef, useState } from 'react';
import { Course } from './Course';
import cytoscape from "cytoscape";

export interface CourseTreeProps {
  // Oh boy here we go
  currentCourse: Course,
  courses: Map<string, Course>,
  onClick: (course: Course) => void,
}

const COREQ_SPACING = 100;
const ROW_SPACING = 100;

function makeElements(props: CourseTreeProps, cx: number, cy: number) {
  let elements: {
    data: { id: string, label: string } | { source: string, target: string, label: string }, position?: { x: number, y: number }
  }[] = [];

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

  return elements;
}


export default function CourseTree2(props: CourseTreeProps) {
  const containerRef = useRef(null);
  const cyRef = useRef<null | cytoscape.Core>(null);

  let [centre, setCentre] = useState({ x: 0, y: 0 });

  let nodeClicked = (event: any) => {
    let node = event.target;

    let course = props.courses.get(node.id());
    setCentre(node.position());
    if (course != null) {
      props.onClick(course);
    }
  };

  useEffect(() => {
    if (containerRef.current !== null) {
      const elements = makeElements(props, centre.x, centre.y);

      if (cyRef.current == null) {
        cyRef.current = cytoscape({
          container: containerRef.current,
          elements: elements,
          style: [
            {
              selector: 'node',
              style: {
                'label': 'data(id)'
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
        for (let el of elements) {
          cyRef.current.add(el);
        }
      }

      return () => {
        for (let el of elements) {
          if (cyRef.current !== null) {
            if (Object.hasOwn(el.data, "id")) {
              var j = cyRef.current.$("#" + el.data.id);
              cyRef.current.remove(j);
            }
          }
        }
      }
    }

    // Maybe cy.remove all previous elements and cy.add all new ones?
    // We can return a destructor from useEffect that can do the destroying
    // The clicked element needs to stay in the same place
  });

  return <div className="w-full h-screen" ref={containerRef} />
}
