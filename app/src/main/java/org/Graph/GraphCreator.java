package org.Graph;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GraphCreator {

    public GraphCreator() {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader("data/Example.json"));
            Type listType = new TypeToken<List<Course>>() {}.getType();
            List<Course> courses = gson.fromJson(reader, listType);
            for (Course course : courses) {
                course.initId();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        GraphCreator gc = new GraphCreator();
    }

}
