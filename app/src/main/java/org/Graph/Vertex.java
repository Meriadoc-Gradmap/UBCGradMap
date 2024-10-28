package org.Graph;

public class Vertex implements Comparable<Vertex> {
  private final int id;

  public Vertex(int id) {
    if (id < 0) {
      throw new IllegalArgumentException("Vertex id cannot be less than 0");
    }
    this.id = id;
  }

  public int id() {
    return this.id;
  }

  @Override
  public boolean equals(Object o) {
    // TODO: write this lol
    return false;
  }

  @Override
  public int compareTo(Vertex o) {
    // TODO: Implement this heheh
    return 0;
  }

  @Override 
  public int hashCode() {
    return this.id;
  }
}
