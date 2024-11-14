# main.java.org.graphapi

This package contains everything the API and its methods for interfacing with the graph object.

## `GraphAPI`
The `GraphAPI` class initializes the graph using `GraphCreator` and passing it a file containing the json database to create the graph from. It then initializes the SpringBoot Application, giving it the created graph as a parameter.

## `Controller`
The `Controller` class handles API requests, by default opening on `localhost:8080/api/` providing methods to get information of a course and a list of all courses.