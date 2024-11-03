package org.Graph;

import java.util.Set;
import java.util.HashSet;

/**
 * Course class.
 *
 * @author Ben Newington
 */
public class Course extends Vertex {

    private String courseCode;
    private String description;
    private String name;
    private int credits;
    private double average;
    private Hours weeklyHours;
    private Set<String> preRequisites;
    private Set<String> dependants;

    /**
     * Creates a course.
     * @param id must be positive.
     */
    public Course(int id) {
        super(id);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public Set<String> getPreRequisites() {
        return new HashSet<>(preRequisites);
    }

    public Set<String> getDependants() {
        return new HashSet<>(dependants);
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

    /* Setters */

    public boolean setCourseCode(String code) {
        if (courseCode == null || courseCode.isEmpty()) {
            this.courseCode = code;
            return true;
        }
        return false;
    }

    public boolean setPreRequisites(Set<String> requisites) {
        if (preRequisites == null || preRequisites.isEmpty()) {
            this.preRequisites = new HashSet<>(requisites);
            return true;
        }
        return false;
    }

    public boolean setDependants(Set<String> dependantSet) {
        if (dependants == null || dependants.isEmpty()) {
            this.dependants = new HashSet<>(dependantSet);
            return true;
        }
        return false;
    }

    public boolean setDescription(String descriptionString) {
        if (description == null || description.isEmpty()) {
            this.description = descriptionString;
            return true;
        }
        return false;
    }

    public boolean setName(String nameString) {
        if (name == null || name.isEmpty()) {
            this.name = nameString;
            return true;
        }
        return false;
    }

    public boolean setCredits(int creditsInt) {
        if (credits == 0) {
            this.credits = creditsInt;
            return true;
        }
        return false;
    }

    public boolean setAverage(double averageDouble) {
        if (average == 0) {
            this.average = averageDouble;
            return true;
        }
        return false;
    }

    public boolean setWeeklyHours(Hours weeklyHoursRecord) {
        if (weeklyHours == null) {
            this.weeklyHours = new Hours(weeklyHoursRecord.lectures(), weeklyHoursRecord.alternating1(),
                    weeklyHoursRecord.labs(), weeklyHoursRecord.alternating2(),
                    weeklyHoursRecord.tutorials(), weeklyHoursRecord.alternating3());
            return true;
        }
        return false;
    }
}
