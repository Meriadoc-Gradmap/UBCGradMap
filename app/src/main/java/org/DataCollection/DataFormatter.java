package org.DataCollection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.io.File;

import org.DataCollection.WebScraper.*;

public class DataFormatter {
    public static void main(String[] args) {
        
        //writeCourseInfoToFile("test.txt", WebScraper.getAllCourses());
        writeGradeInfoToFile("grades.csv", WebScraper.getAllGrades());
    }

    public static void writeCourseInfoToFile(String outputFileName, List<Course> courseList) {
        File file = new File("app/src/main/java/org/DataCollection/DataCache", outputFileName);
        try {
            FileWriter fwrite = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fwrite);
            for (Course c : courseList) {
                writer.write("Title: " + c.Title());
                writer.newLine();
                writer.write("Description: " + c.Description());
                writer.newLine();
            }
            writer.close();
            fwrite.close();
        } catch (IOException e) {
            System.out.println("There was an error writing to the file");
        }
    }

    public static void writeGradeInfoToFile(String outputFileName, Map<String, Double> gradeMap) {
        File file = new File("app/src/main/java/org/DataCollection/DataCache", outputFileName);
        try {
            FileWriter fwrite = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fwrite);
            writer.write("Course, Grade");
            writer.newLine();
            for (String s : gradeMap.keySet()) {
                writer.write(s + ",");
                writer.write(gradeMap.get(s) + "");
                writer.newLine();
            }
            writer.close();
            fwrite.close();
        } catch (IOException e) {
            System.out.println("There was an error writing to the file");
        }
    }

    public static void readCourseInfoFromFile(String inputFileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
