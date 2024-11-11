package org.Graph;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class CourseGraph {
    private final Set<Course> courseSet;
    private final double[][] gradesMatrix;
    private final String[] names;

    private final Map<String, Integer> codeToId = new HashMap<>();

    /**
     * The constructor should take a set of courses, where each course
     * contains information for its pre reqs and dependants. From those sets
     *   we should be able to build the matrix using the map of course code to id.
     *
     */
    public CourseGraph(Set<Course> courses) {
        this.courseSet = courses;
        gradesMatrix = new double[courseSet.size()][courseSet.size()];
        names = new String[courseSet.size()];

        initCourseVertices();
        initCourseEdges();
    }

    private void initCourseVertices() {
        for (Course course : courseSet) {
            if (course.id() > courseSet.size() - 1) {
                throw new IllegalArgumentException("Course id must be from 0 to courses.size() - 1");
            }
            codeToId.put(course.getCourseCode(), course.id());
            names[course.id()] = course.getCourseCode();
        }
    }

    private void initCourseEdges() {
        for (Course course : courseSet) {
            for (String preReq : course.getPreRequisites()) {
                if (!codeToId.containsKey(preReq)) {
                    throw new IllegalArgumentException("Pre-requisite must be in graph"); //TODO: should I do this or not throw an error?
                }
                gradesMatrix[codeToId.get(preReq)][course.id()] = 100 - course.getAverage();
            }
            for (String dependant : course.getPostRequisites()) {
                if (!codeToId.containsKey(dependant)) {
                    throw new IllegalArgumentException("Post-requisite must be in graph");
                }
                gradesMatrix[course.id()][codeToId.get(dependant)] = 100 - getCourse(dependant).getAverage();
            }
        }
    }

    public Course getCourse(String code) {
        return getCourse(codeToId.get(code));
    }

    private Course getCourse(int id) {
        return courseSet.stream().
                filter(course -> course.id() == id).
                findFirst().
                orElseThrow();

    }

    public String[] getNames() {
        return names.clone();
    }

    public Set<String> getPreRequisites(String code) {
        Set<String> preRequisites = new HashSet<>();
        for (int i = 0; i < courseSet.size(); i++) {
            if (gradesMatrix[i][codeToId.get(code)] > 0) {
                preRequisites.add(names[i]);
            }
        }
        return preRequisites;
    }

    public Set<String> getPostRequisites(String code) {
        Set<String> postRequisites = new HashSet<>();
        for (int i = 0; i < courseSet.size(); i++) {
            if (gradesMatrix[codeToId.get(code)][i] > 0) {
                postRequisites.add(names[i]);
            }
        }
        return postRequisites;
    }

    public Set<String> getCoRequisites(String code) {
        Set<String> dependants = getPostRequisites(code);
        Set<String> coRequisites = getPreRequisites(code);
        coRequisites.retainAll(dependants);
        return new HashSet<>(coRequisites);
    }

}
