package org.graph;

/**
 * Contains information for a schedule of a course.
 * @param lectures hours of lectures
 * @param alternating1 true if it is alternating
 * @param labs hours of labs
 * @param alternating2 true if labs are alternating
 * @param tutorials hours of tutorials
 * @param alternating3 true if tutorials are alternating
 */
public record Hours(int lectures, boolean alternating1, int labs, boolean alternating2, int tutorials,
        boolean alternating3) {
}
