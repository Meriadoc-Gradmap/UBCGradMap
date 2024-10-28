package org.Graph;

import java.util.Set;
import java.util.HashSet;

public class Course extends Vertex {

  private String courseCode;
  private String description;
  private String name;
  private int credits;
  private double average;
  private Hours weeklyHours;
  private Set<String> preRequisites;
  private Set<String> dependants;

  public Course(int id) {
    super(id);
  }

  // TODO: create getters and setters for all field

}
