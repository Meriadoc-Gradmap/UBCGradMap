package org.graph;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;

/**
 * Creates a course graph, given a json file.
 *
 * @author Ben Newington.
 */
public class GraphCreator {

    /**
     * Creates a graph with a given json file.
     * @param fileName the path of the json file. Must contain courses formatted for the course class.
     * @return a graph of the courses in the json file.
     * @throws IllegalArgumentException if the file did not open correctly.
     */
    public static CourseGraph createGraph(String fileName) {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(fileName));
            Type listType = new TypeToken<List<Course>>() {
            }.getType();
            List<Course> courses = gson.fromJson(reader, listType);
            for (Course course : courses) {
                course.initId();
            }
            return new CourseGraph(new HashSet<>(courses));

        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException();
        }
    }
}
