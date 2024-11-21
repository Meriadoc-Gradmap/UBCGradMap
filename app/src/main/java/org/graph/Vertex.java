package org.graph;

/**
 * Vertex class.
 *
 * @author Ben Newington.
 */
public class Vertex {
    private int id;

    /**
     * Creates a vertex with an id.
     * @param id must be greater than 0
     */
    public Vertex(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Vertex id cannot be less than 0");
        }
        this.id = id;
    }

    /**
     * Finds the id.
     * @return the id
     */
    public int id() {
        return this.id;
    }

    /**
     * Sets the vertex id. Can only be used be subclasses.
     * @param id must be greater than 0.
     */
    protected void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Vertex id cannot be less than 0");
        }
        this.id = id;
    }

    /**
     * Equals override to check id.
     * @param o other vertex
     * @return true if they have the same id
     */
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
