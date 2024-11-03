package org.Graph;

import java.util.Set;
import java.util.HashSet;

/**
 * Course class.
 *
 * @author Ben Newington
 */
public class Course extends Vertex {

    private String code = "";
    private String name = "";
    private int[] credits = null;
    private String description = "";
    private Set<String> prerequisites = null;
    private Set<String> postrequisites = null;
    private boolean cdf = false;
    private Hours schedule = null;
    private Others others = null;
    private static int id = 0;

    /**
     * Creates a course.
     */
    public Course(String code, String name, int[] credits, String description,
                  Set<String> prerequisites, Set<String> postrequisites, boolean cdf,
                  Hours schedule, Others othersRecord) {
        super(id);
        id++;

        this.code = code;
        this.description = description;
        this.name = name;
        this.credits = credits;
        this.schedule = schedule.copy();
        this.prerequisites = new HashSet<>(prerequisites);
        this.postrequisites = new HashSet<>(postrequisites);
        this.others = othersRecord;
        this.cdf = cdf;
    }

    public void initId() {
        super.setId(id);
        id++;
    }

    public String getCourseCode() {
        return code;
    }

    public Set<String> getPreRequisites() {
        return new HashSet<>(prerequisites);
    }

    public Set<String> getPostRequisites() {
        return new HashSet<>(postrequisites);
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int[] getCredits() {
        return credits;
    }

    public double getAverage() {
        return others.average();
    }

    public Hours getWeeklyHours() {
        return schedule.copy();
    }

    public Others getOthers() {
        return others.copy();
    }

    public boolean isCdf() {
        return cdf;
    }
}
