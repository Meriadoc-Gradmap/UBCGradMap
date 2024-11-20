import React, { useEffect, useLayoutEffect, useRef, useState } from 'react';
import { Course, Position } from './Course';
import cytoscape from "cytoscape";

export interface CourseTreeProps {
  // Oh boy here we go
  coursePath: Course[];
  onClick: (course: string) => void,
}

const COREQ_SPACING = 100;
const ROW_SPACING = 125;

enum EDGE_TYPE {
  PREREQ, COREQ, POSTREQ, NONE, PATH
}

const NODE_COLOUR = "#5FB4D5";
const DELECTED_COLOUR = "#002145";

/**
  * Goals:
  * For the most part, be deterministic
  * Except, never move a node
  */
function makeElements(props: CourseTreeProps, oldCoursePos: Map<string, Position>) {
  
  if (props.coursePath.length < 1) {
    console.log("No course selected");
    return {elements: [], oldCoursePos: oldCoursePos};
  }

  let elements: {
    data: { id: string, label: string } | { source: string, target: string, label: string }, position?: { x: number, y: number }, style?: {'line-color':string, 'target-arrow-color':string} | {'background-color':string}
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

  let addCourse = (course: string | undefined, parent: string | undefined, pos: { x: number, y: number }, edge_type: EDGE_TYPE, color: string = NODE_COLOUR) => {
    if (course == null) {
      return;
    }

    if (!coursePos.has(course)) {
      // Add the element
      elements.push({
        data: { id: course, label: course},
        position: pos,
        style: {
          'background-color': color
        }
      })

      coursePos.set(course, pos);
    }

    // Add the edge
    if (parent != undefined) {
      if (edge_type == EDGE_TYPE.PREREQ || edge_type == EDGE_TYPE.COREQ) {
        elements.push({ data: { source: course, target: parent, label: "" } });
      }
      if (edge_type == EDGE_TYPE.POSTREQ || edge_type == EDGE_TYPE.COREQ) {
        elements.push({ data: { source: parent, target: course, label: "" } });
      }
      if (edge_type == EDGE_TYPE.PATH) {
        elements.push({ data: { source: parent, target: course, label: "" } , style: {"line-color": "red", "target-arrow-color": "red"} });
      }
    }
  };



  let addRow = (courses: string[], y: number, edge_type: EDGE_TYPE) => {
    let startPos = cx - COREQ_SPACING * (Math.ceil(courses.length / 2))

    for (let course of courses) {
      // Bugs in the rest of the stack sometimes give me shit data
      // So just ignore it
      if (course == "") {
        continue;
      }

      // Calculate position
      let pos = {x:startPos, y:y};
      while (isCollision(pos)) {
        startPos += COREQ_SPACING;
        pos = {x:startPos, y:y};
      }

      let {changed, realPos} = getPosition(course, pos);
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
      let oldPos = oldCoursePos.get(course.code) ?? {x: 0, y: 0};
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


  return {elements: elements, oldCoursePos: coursePos};
}


export default function CourseTree2(props: CourseTreeProps) {
  const containerRef = useRef(null);
  const cyRef = useRef<null | cytoscape.Core>(null);

  let [oldCoursePos, setOldCoursePos] = useState(new Map<string, Position>());

  let nodeClicked = (event: any) => {
    let node = event.target;

    props.onClick(node.id());
  };

  useEffect(() => {
    if (containerRef.current !== null) {
      let newOldCoursePos = new Map<string, Position>();
      if (props.coursePath.length > 1) {
        newOldCoursePos = oldCoursePos;
      }
      let {elements, oldCoursePos: _oldCoursePos} = makeElements(props, newOldCoursePos);
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
        for (let el of elements) {
          cyRef.current.add(el);
        }

        if (props.coursePath.length <= 1) {
          cyRef.current.fit();
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
