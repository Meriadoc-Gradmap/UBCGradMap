package org.Graph;

public record Others(double average, String professor) {

    public Others copy() {
        return new Others(this.average(), this.professor());
    }
}
