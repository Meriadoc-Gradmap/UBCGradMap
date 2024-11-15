package org.graph;

import java.util.Set;
import java.util.HashSet;

/**
 * Course class.
 *
 * @author Ben Newington
 */
public class Course extends Vertex {

    private final String code;
    private final String name;
    private final double[] credits;
    private final String description;
    private final Set<String> prerequisites;
    private final boolean cdf;
    private final Hours schedule;
    private final Others others;
    private static int id = 0;
    private boolean hasId;
    private final Set<String> corequisites;

    /**
     * Creates a course.
     * 
     * @param code           course code "XXXX###"
     * @param name           course name
     * @param credits        credits for each version of the course
     * @param description    description
     * @param prerequisites  list of course codes that are prerequisites
     * @param corequisites   list of course codes that are corequisites
     * @param cdf            if the course is credit d fail
     * @param schedule       record of how many hours per week the course is
     * @param othersRecord   other information
     * @param id             must be greater than 0
     */
    public Course(String code, String name, double[] credits, String description,
            Set<String> prerequisites, Set<String> corequisites, boolean cdf,
            Hours schedule, Others othersRecord, int id) {
        super(id);
        hasId = true;

        this.code = code;
        this.description = description;
        this.name = name;
        this.credits = credits.clone();
        this.schedule = schedule.copy();
        this.prerequisites = new HashSet<>(prerequisites);
        this.corequisites = new HashSet<>(corequisites);
        this.others = othersRecord.copy();
        this.cdf = cdf;
    }

    /**
     * Sets the id to the previous course's id plus one.
     */
    public void initId() {
        if (!hasId) {
            super.setId(id);
            id++;
            hasId = true;
        }
    }

    /**
     * Gets the course code.
     * 
     * @return course code
     */
    public String getCourseCode() {
        return code;
    }

    /**
     * Gets the pre-requisites.
     * 
     * @return a set of pre-requisites
     */
    public Set<String> getPreRequisites() {
        return new HashSet<>(prerequisites);
    }

    /**
     * Gets the co-requisites.
     * @return a set of co-requisites.
     */
    public Set<String> getCorequisites() {
        return new HashSet<>(corequisites);
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the number of credits for each course version.
     * 
     * @return an array of the number of credits
     */
    public double[] getCredits() {
        return credits.clone();
    }

    /**
     * Gets the average.
     * 
     * @return the average
     */
    public double getAverage() {
        return others.average();
    }

    /**
     * Gets the weekly hours.
     * 
     * @return the weekly hours
     */
    public Hours getWeeklyHours() {
        return schedule.copy();
    }

    /**
     * Gets the other information.
     * 
     * @return other information
     */
    public Others getOthers() {
        return others.copy();
    }

    /**
     * Returns <code>true</code> if the course is a credit/d/fail.
     * 
     * @return <code>true</code> if the course is a credit/d/fail,
     *         <code>false</code> otherwise
     */
    public boolean isCdf() {
        return cdf;
    }
}
