package org.graph;

public record Others(double average, String professor) {

    public Others copy() {
        return new Others(this.average(), this.professor());
    }
}
