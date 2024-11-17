# main.java.org.graphapi

This package contains everything the API and its methods for interfacing with the graph object.

## `GraphAPI`
The `GraphAPI` class runs the `Controller` class, initializing the SpringBoot API application.

## `Controller`
The `Controller` class handles API requests, by default opening on `localhost:8080/api/` providing methods to get information of a course and a list of all courses. It also initializes the graph on startup from a pre-determined file.