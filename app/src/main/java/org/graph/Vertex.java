package org.graph;

/**
 * Vertex class.
 */
public class Vertex {
    private int id;

    public Vertex(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Vertex id cannot be less than 0");
        }
        this.id = id;
    }

    public int id() {
        return this.id;
    }

    protected void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Vertex id cannot be less than 0");
        }
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Vertex other) {
            return (this.id == other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}
