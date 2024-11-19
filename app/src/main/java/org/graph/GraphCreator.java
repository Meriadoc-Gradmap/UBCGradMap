package org.graph;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class GraphCreator {
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
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        CourseGraph gc = createGraph("data/COURSE_INFO.json");
        System.out.println(gc.getAllPreRequisites("CPEN-320"));
        System.out.println("h");
    }

}
