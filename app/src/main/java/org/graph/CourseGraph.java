package org.graph;

import java.util.*;

public class CourseGraph {
    private final Set<Course> courseSet;
    private final double[][] gradesMatrix;
    private final String[] courseCodes;

    private final Map<String, Integer> codeToId = new HashMap<>();

    /**
     * The constructor should take a set of courses, where each course
     * contains information for its pre reqs and dependants. From those sets
     * we should be able to build the matrix using the map of course code to id.
     *
     */
    public CourseGraph(Set<Course> courses) {
        this.courseSet = courses;
        gradesMatrix = new double[courseSet.size()][courseSet.size()];
        courseCodes = new String[courseSet.size()];

        initCourseVertices();
        initCourseEdges();
    }

    private void initCourseVertices() {
        for (Course course : courseSet) {
            if (course.id() > courseSet.size() - 1) {
                throw new IllegalArgumentException("Course id must be from 0 to courses.size() - 1");
            }
            codeToId.put(course.getCourseCode(), course.id());
            courseCodes[course.id()] = course.getCourseCode();
        }
    }

    private void initCourseEdges() {
        for (Course course : courseSet) {
            for (String preReq : course.getPreRequisites()) {
                if (codeToId.containsKey(preReq)) {
                    gradesMatrix[codeToId.get(preReq)][course.id()] = 101 - course.getAverage();
                }
            }
        }
    }

    public Course getCourse(String code) {
        return getCourse(codeToId.get(code));
    }

    private Course getCourse(int id) {
        return courseSet.stream().filter(course -> course.id() == id).findFirst().orElseThrow();

    }

    public String[] getCodes() {
        return courseCodes.clone();
    }

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

    public Set<String> getPreRequisites(String code) {
        Set<String> preRequisites = new HashSet<>();
        for (int i = 0; i < courseSet.size(); i++) {
            if (gradesMatrix[i][codeToId.get(code)] > 0) {
                preRequisites.add(courseCodes[i]);
            }
        }
        return preRequisites;
    }

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

    public Set<String> getPostRequisites(String code) {
        Set<String> postRequisites = new HashSet<>();
        for (int i = 0; i < courseSet.size(); i++) {
            if (gradesMatrix[codeToId.get(code)][i] > 0) {
                postRequisites.add(courseCodes[i]);
            }
        }
        return postRequisites;
    }

    public Set<String> getCoRequisites(String code) {
        return new HashSet<>(getCourse(code).getCorequisites());
    }

}
