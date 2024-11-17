package org.graphapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.graph.CourseGraph;
import org.graph.GraphCreator;

@SpringBootApplication
public class GraphApi {

    public static void main(String[] args) {
        SpringApplication.run(GraphApi.class, args);
    }
}
