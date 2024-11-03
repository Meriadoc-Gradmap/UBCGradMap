package org.Graph;

import java.util.Set;
import java.util.HashSet;

/**
 * Course class.
 *
 * @author Ben Newington
 */
public class Course extends Vertex {

    private final String courseCode;
    private final String description;
    private final String name;
    private final int credits;
    private final double average;
    private final Hours weeklyHours;
    private final Set<String> preRequisites;
    private final Set<String> postRequisites;

    /**
     * Creates a course.
     * @param id must be positive.
     */
    public Course(int id, String courseCode, String description, String name,
                  int credits, double average, Hours weeklyHoursRecord, Set<String> preRequisites,
                  Set<String> postRequisites) {
        super(id);
        this.courseCode = courseCode;
        this.description = description;
        this.name = name;
        this.credits = credits;
        this.average = average;
        this.weeklyHours = weeklyHoursRecord.copy();
        this.preRequisites = new HashSet<>(preRequisites);
        this.postRequisites = new HashSet<>(postRequisites);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public Set<String> getPreRequisites() {
        return new HashSet<>(preRequisites);
    }

    public Set<String> getPostRequisites() {
        return new HashSet<>(postRequisites);
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public double getAverage() {
        return average;
    }

    public Hours getWeeklyHours() {
        return weeklyHours;
    }
}
