# main.java.org.graphapi

This package contains everything the API and its methods for interfacing with the graph object. The Controller file is where api calls are actually handled. All files here are authored by Iain Griesdale.

## `GraphAPI`
The `GraphAPI` class runs the `Controller` class, initializing the SpringBoot API application.

## `Controller`
The `Controller` class handles API requests, by default opening on `localhost:8080/api/` providing methods to get information of a course and a list of all courses. It also initializes the graph on startup from a pre-determined file. There are three methods
### `getTest`
is a basic method to test that the api is online and can be accessed
### `getCourse`
Gets all required information for a specified course in json format.
### `getAllCourses`
Gets a list of all the courses in json.
### `initializeGraph`
Runs on startup and initializes the graph from the `GraphCreator`
