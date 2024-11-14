import { Course } from './Course';

/**
  * A little helper class that threads together units
  */
class Graph {
  elements: {
    data: { id: string, label: string } | { source: string, target: string, label: string }, position?: { x: number, y: number }
  }[] = [];

  constructor() {

  }

  add(params: Unit) {

  }
}

interface Node {
  data: { id: string, label: string },
  position: { x: number, y: number }
}

interface Edge {
  data: { source: string, target: string, label: string },
}

const SPACING = 100;

class Unit {
  edges: Edge[] = [];
  nodes: Map<string, Node> = new Map<string, Node>();
  minX: number = 0;
  minY: number = 0;
  maxX: number = 0;
  maxY: number = 0;

  constructor(course: Course) {
    let startPos = 0 - SPACING * (Math.ceil(course.corequisites.length / 2))
  }

  addCourse(courseCode: string, parent: string | null, pos: { x: number, y: number }) {
    this.nodes.set(courseCode, {
      data: { id: courseCode, label: courseCode },
      position: pos
    });

    if (parent !== null) {
      this.edges.push({ data: { source: courseCode, target: parent, label: "" } });
    }
  }

  // TODO: Read this
  /*
   * Each Unit generates a layout for a specific course independantly
   * The units are then fed into the graph back to front,
   * And they are placed in the correct place by the graph.
   * The graph also makes sure there are no duplicate nodes 
   * by wiring the correct nodes together.
   */
}
