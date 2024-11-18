package org.graph;

public record Others(double grade) {

    public Others copy() {
        return new Others(this.grade());
    }
}
