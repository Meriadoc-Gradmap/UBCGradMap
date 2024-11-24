package org.graph;

import java.util.*;

/**
 * Graph of courses.
 *
 * @author Ben Newington
 */
public class CourseGraph {
    private final Set<Course> courseSet;
    private final double[][] gradesMatrix;
    private final String[] courseCodes;

    private static final int EDGE_AVERAGE_LENGTH = 101;

    private final Map<String, Integer> codeToId = new HashMap<>();

    /**
     * Takes a set of courses to be inside the graph.
     * @param courses must contain all ids from {@code 0-courseSet.size()}
     * @throws IllegalArgumentException if {@code courses} are invalid
     */
    public CourseGraph(Set<Course> courses) {
        this.courseSet = courses;
        gradesMatrix = new double[courseSet.size()][courseSet.size()];
        courseCodes = new String[courseSet.size()];

        Course.resetID(); // added as if you do not do this the code does not function if
                          // you run two CourseGraph
        initCourseVertices();
        initCourseEdges();
    }

    /**
     * Adds the vertices to the graph.
     * @throws IllegalArgumentException if {@code courses} are invalid
     */
    private void initCourseVertices() {
        for (Course course : courseSet) {
            if (course.id() > courseSet.size() - 1  || course.id() < 0) {
                throw new IllegalArgumentException("Course id must be from 0 to courses.size() - 1");
            }
            codeToId.put(course.getCourseCode(), course.id());
            courseCodes[course.id()] = course.getCourseCode();
        }
    }

    /**
     * Adds the edges to the graph with the length set to {@code 101-average} for the ability to add pathfinding later.
     */
    private void initCourseEdges() {
        for (Course course : courseSet) {
            for (String preReq : course.getPreRequisites()) {
                if (codeToId.containsKey(preReq)) {
                    gradesMatrix[codeToId.get(preReq)][course.id()] = EDGE_AVERAGE_LENGTH - course.getAverage();
                }
            }
        }
    }

    /**
     * Finds the course with the given course code.
     * @param code the course code of the course to search for
     * @return the course with {@code code} in the graph
     * @throws IllegalArgumentException if {@code code} is not in graph
     */
    public Course getCourse(String code) {
        if (codeToId.containsKey(code)) {
            return getCourse(codeToId.get(code));
        } else {
            throw new IllegalArgumentException("Cannot find course");
        }
    }

    /**
     * Finds the course with the given course id.
     * @param id the course id for the course to search for
     * @return the course with {@code id} in the graph
     * @throws IllegalArgumentException if {@code code} is not in graph
     */
    private Course getCourse(int id) {
        return courseSet.stream().filter(course -> course.id() == id).findFirst().orElseThrow();

    }

    /**
     * Returns a copy of all the codes in the graph.
     * @return a copy of every course code in the graph
     */
    public String[] getCodes() {
        return courseCodes.clone();
    }

    /**
     * Finds every pre req for a given course.
     * @param code the course code
     * @return A set of every pre req for a given course code. If the code doesn't exist, returns an empty set.
     */
    public Set<String> getAllPreRequisites(String code) {
        Queue<String> queue = new PriorityQueue<>();
        Set<String> preRequisites = new HashSet<>();
        queue.add(code);
        while (!queue.isEmpty()) {
            Set<String> preReq = getPreRequisites(queue.poll());
            preRequisites.addAll(preReq);
            queue.addAll(preReq);
        }

        return preRequisites;
    }

    /**
     * Finds the immediate pre reqs for a given course.
     * @param code the course code
     * @return A set of immediate pre reqs for a given course code. If the code doesn't exist, returns an empty set.
     */
    public Set<String> getPreRequisites(String code) {
        if (codeToId.containsKey(code)) {
            return getCourse(code).getPreRequisites();
        } else {
            return new HashSet<>();
        }
    }

    /**
     * Finds every post req for a given course.
     * @param code the course code
     * @return A set of every post req for a given course code. If the code doesn't exist, returns an empty set.
     */
    public Set<String> getAllPostRequisites(String code) {
        Queue<String> queue = new PriorityQueue<>();
        Set<String> postRequisites = new HashSet<>();
        queue.add(code);
        while (!queue.isEmpty()) {
            Set<String> preReq = getPostRequisites(queue.poll());
            postRequisites.addAll(preReq);
            queue.addAll(preReq);
        }

        return postRequisites;
    }

    /**
     * Finds the immediate post reqs for a given course.
     * @param code the course code
     * @return A set of immediate post reqs for a given course code. If the code doesn't exist, returns an empty set.
     */
    public Set<String> getPostRequisites(String code) {
        Set<String> postRequisites = new HashSet<>();
        if (codeToId.containsKey(code)) {
            for (int i = 0; i < courseSet.size(); i++) {
                if (gradesMatrix[codeToId.get(code)][i] > 0) {
                    postRequisites.add(courseCodes[i]);
                }
            }
        }

        return postRequisites;
    }

    /**
     * Finds the immediate co reqs for a given course.
     * @param code the course code
     * @return A set of immediate co reqs for a given course code.
     * @throws IllegalArgumentException if {@code code} doesn't exist
     */
    public Set<String> getCoRequisites(String code) {
        return getCourse(code).getCorequisites();
    }

}
