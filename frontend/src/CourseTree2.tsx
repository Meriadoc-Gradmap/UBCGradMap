import React, { useEffect, useLayoutEffect, useRef, useState } from 'react';
import { Course, Position } from './Course';
import cytoscape from "cytoscape";

export interface CourseTreeProps {
  // Oh boy here we go
  coursePath: Course[];
  onClick: (course: string) => void,
}

const COREQ_SPACING = 100;
const ROW_SPACING = 100;

/**
  * Goals:
  * For the most part, be deterministic
  * Except, never move a node
  */
function makeElements(props: CourseTreeProps, oldCoursePos: Map<String, Position>) {
  
  if (props.coursePath.length < 1) {
    console.error("No course selected");
    return {elements: [], oldCoursePos: oldCoursePos};
  }

  let elements: {
    data: { id: string, label: string } | { source: string, target: string, label: string }, position?: { x: number, y: number }
  }[] = [];

  let coursePos: Map<string, { x: number, y: number }> = new Map<string, { x: number, y: number }>();

  let currentCourse = props.coursePath[props.coursePath.length - 1];
  let cx = 0, cy = 0;

  if (oldCoursePos.has(currentCourse.code)) {
    // Center it around the current position then
    cx = (oldCoursePos.get(currentCourse.code) ?? {x: 0, y:0}).x ;
    cy = (oldCoursePos.get(currentCourse.code) ?? {x: 0, y:0}).y ;
  }
  else {
    oldCoursePos.set(currentCourse.code, {x: cx, y: cy});
  }

  let isCollision = (pos: Position) => {
    for (let course of props.coursePath) {
      if (oldCoursePos.has(course.code)) {
        let oldPos = oldCoursePos.get(course.code) ?? {x: 0, y: 0};
        if (oldPos.x == pos.x && oldPos.y == pos.y) {
          return true;
        }
      }
    }

    return false;
  }

  let getPosition = (sampleCourse: string, samplePos: Position) => {
    if (sampleCourse == currentCourse.code) {
      return {changed: false, realPos: {x: cx, y: cx}};
    }

    for (let course of props.coursePath) {
      if (course.code != sampleCourse) {
        continue;
      }
      if (oldCoursePos.has(course.code)) {
        let oldPos = oldCoursePos.get(course.code) ?? {x: 0, y: 0};

        return {changed: true, realPos: oldPos};
      }
    }

    return {changed: false, realPos: samplePos};
  }

  let addCourse = (course: string | undefined, parent: string | undefined, pos: { x: number, y: number }) => {
    if (course == null) {
      return;
    }

    if (!coursePos.has(course)) {
      // Add the element
      elements.push({
        data: { id: course, label: course},
        position: pos,
      })

      coursePos.set(course, pos);
    }

    // Add the edge
    if (parent != undefined) {
      elements.push({ data: { source: course, target: parent, label: "" } });
    }
  };

  let addRow = (courses: string[], y: number) => {
    let startPos = cx - COREQ_SPACING * (Math.ceil(courses.length / 2))

    for (let course of courses) {
      // Calculate position
      let pos = {x:startPos, y:y};
      while (isCollision(pos)) {
        startPos += COREQ_SPACING;
        pos = {x:startPos, y:y};
      }

      let {changed, realPos} = getPosition(course, pos);
      addCourse(course, currentCourse.code, realPos);

      if (!changed) {
        startPos += COREQ_SPACING;
      }
    }
  }

  // Add the rest of the path
  let lastElement = undefined;
  for (let course of props.coursePath) {
    if (oldCoursePos.has(course.code)) {
      let oldPos = oldCoursePos.get(course.code) ?? {x: 0, y: 0};
      addCourse(course.code, lastElement, oldPos);

      lastElement = course.code;
    }
    else {
      console.error("This should never happen");
    }
  }

  // Add everything else
  addRow(currentCourse.corequisites, cy);
  addRow(currentCourse.prerequisites, cy - ROW_SPACING);
  addRow(currentCourse.postrequisites, cy + ROW_SPACING);


  return {elements: elements, oldCoursePos: coursePos};
}


export default function CourseTree2(props: CourseTreeProps) {
  const containerRef = useRef(null);
  const cyRef = useRef<null | cytoscape.Core>(null);

  let [oldCoursePos, setOldCoursePos] = useState(new Map<String, Position>());

  let nodeClicked = (event: any) => {
    let node = event.target;

    props.onClick(node.id());
  };

  useEffect(() => {
    if (containerRef.current !== null) {
      let {elements, oldCoursePos: _oldCoursePos} = makeElements(props, oldCoursePos);
      setOldCoursePos(_oldCoursePos);

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
  }, [props]);

  return <div className="w-full h-screen" ref={containerRef} />
}
