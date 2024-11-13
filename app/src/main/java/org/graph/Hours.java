package org.graph;

public record Hours(int lectures, boolean alternating1, int labs, boolean alternating2, int tutorials,
        boolean alternating3) {

    public Hours copy() {
        return new Hours(this.lectures(), this.alternating1(),
                this.labs(), this.alternating2(),
                this.tutorials(), this.alternating3());
    }
}
