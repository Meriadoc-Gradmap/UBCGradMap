package org.Graph;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class CourseGraph {
  private final Set<Course> courseSet;
  private final double[][] gradesMatrix;

  private final Map<String, Integer> codeToId = new HashMap<>();
  // id's are numbered 0 to n where n is the number of courses

  /**
   * The constructor should take a set of courses, where each course
   * contains information for its pre reqs and dependants. From those sets
   * we should be able to build the matrix using the map of course code to id.
   *
   */
  public CourseGraph(Set<Course> courses) {
    this.courseSet = courses;
    gradesMatrix = new double[courseSet.size()][courseSet.size()];
    // TODO: implement the constructor.

  }
}
