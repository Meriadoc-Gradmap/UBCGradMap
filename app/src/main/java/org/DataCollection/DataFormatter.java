package org.DataCollection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;

import org.DataCollection.WebScraper.*;

import com.google.gson.Gson;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;


public class DataFormatter {

    public static final int INDEX_VAL0 = 0;
    public static final int INDEX_VAL1 = 1;
    public static final int INDEX_VAL2 = 2;
    public static final int INDEX_VAL3 = 3;
    public static final int INDEX_VAL4 = 4;
    public static final int INDEX_VAL5 = 5;
    public static final int INDEX_VAL6 = 6;
    public static final int INDEX_VAL7 = 7;
    public static final int INDEX_VAL8 = 8;
    public static final int INDEX_VAL9 = 9;
    public static final int INDEX_VAL10 = 10;
    private static final int COLOR_VALUE = 33;

    public static void main(String[] args) {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
        
        createJsonNoCache();
    }

    record FullCourse(
        String code,
        String name,
        double[] credits,
        String decription,
        String[] prerequisites,
        String[] corequisites,
        boolean cdf,
        Schedule schedule,
        Others other
    ) {

    }

    record Schedule(
        int lectures,
        boolean alternating1,
        int labs,
        boolean alternating2,
        int tutorials,
        boolean alternating3
    ) {

    }

    record Others(
        double grade
    ) {

    }

    public static void createJson(List<Course> courseList, Map<String, Double> gradeMap) {
        Pattern titlePattern = Pattern.compile("([A-Za-z]+).+(\\d\\d\\d).+\\((\\d+\\.*\\d*)-?(\\d+\\.*\\d*)?\\)(.+)");
        Pattern schedulePattern = Pattern.compile("\\[(\\d)(\\*?)-(\\d)(\\*?)-(\\d)(\\*?)\\]");
        Pattern preReqPattern = Pattern.compile("(?:Pre-?requisites?:\\s*([A-Za-z]+? \\d++[^.]*)?)");
        Pattern coReqPattern = Pattern.compile("(?:Co-?requisites?:\\s*([A-Za-z]+? \\d++[^.]*)?)");

        List<FullCourse> masterList = new ArrayList<>();

        ProgressBarBuilder pbb = ProgressBar.builder().
                        setStyle(ProgressBarStyle.builder().
                        colorCode((byte) COLOR_VALUE).
                        leftBracket("[").
                        rightBracket("]").
                        block('=').
                        rightSideFractionSymbol('>').
                        build()
                        );

        for (Course c : ProgressBar.wrap(courseList, pbb)) {
            String code = "";
            double creditsLOW = -1;
            double creditsHIGH = -1;
            String name = "";
            String description = "";
            int lectures = -1;
            boolean alternating1 = false;
            int labs = -1;
            boolean alternating2 = false;
            int tutorials = -1;
            boolean alternating3 = false;
            String prerequisites = "";
            String corequisites = "";
            boolean cdf = false;
            double grade = -1;

            Matcher titleMatcher = titlePattern.matcher(c.Title());
            if (titleMatcher.find()) {
                code = titleMatcher.group(1) + "-" + titleMatcher.group(2);
                creditsLOW = Double.parseDouble(titleMatcher.group(INDEX_VAL3));
                if (titleMatcher.group(INDEX_VAL4) != null) {
                    creditsHIGH = Double.parseDouble(titleMatcher.group(INDEX_VAL4));
                }
                name = titleMatcher.group(INDEX_VAL5).trim();
            }

            String desc = c.Description();

            description = desc.split("Pre-?requisites?:|Co-?requisites?:|\\[")[0];

            Matcher scheduleMatcher = schedulePattern.matcher(desc);

            if (scheduleMatcher.find()) {
                lectures = Integer.parseInt(scheduleMatcher.group(1));
                alternating1 = scheduleMatcher.group(2) != null && scheduleMatcher.group(2).contains("*");
                labs = Integer.parseInt(scheduleMatcher.group(3));
                alternating2 = scheduleMatcher.group(4) != null && scheduleMatcher.group(4).contains("*");
                tutorials = Integer.parseInt(scheduleMatcher.group(5));
                alternating3 = scheduleMatcher.group(6) != null && scheduleMatcher.group(6).contains("*");
            }

            Matcher preReqMatcher = preReqPattern.matcher(desc);

            if (preReqMatcher.find() && preReqMatcher.group(1) != null) {
                prerequisites = preReqMatcher.group(1).replaceAll("([A-Za-z]+) (\\d+)", "$1-$2");
                prerequisites = prerequisites.replaceAll("(,) ([A-Za-z]+)", ",$2");
                prerequisites = prerequisites.replaceAll("[^A-Z0-9-]", " ").trim();
                prerequisites = prerequisites.replaceAll("\\s+", ",");
                prerequisites = prerequisites.replaceAll(" ", "").trim();
            } 

            Matcher coReqMatcher = coReqPattern.matcher(desc);

            if (coReqMatcher.find() && coReqMatcher.group(1) != null) {
                corequisites = coReqMatcher.group(1).replaceAll("([A-Za-z]+) (\\d+)", "$1-$2");
                corequisites = corequisites.replaceAll("(,) ([A-Za-z]+)", ",$2");
                corequisites = corequisites.replaceAll("[^A-Z0-9-, ]", " ").trim();
                corequisites = corequisites.replaceAll("\\s+", ",");
                corequisites = corequisites.replaceAll(" ", "").trim();
            } 
            
            if (desc.contains("This course is not eligible for Credit/D/Fail grading.")) {
                cdf = false;
            } 

            if (gradeMap.containsKey(code)) {
                grade = gradeMap.get(code);
            }

            Schedule schedule = new Schedule(lectures, alternating1, labs, alternating2, tutorials, alternating3);
            Others others = new Others(grade);

            double[] credits;
            if (creditsHIGH != -1) {
                credits = new double[(int) (creditsHIGH - creditsLOW + 2)];

                for (int i = 0; i < (int) (creditsHIGH - creditsLOW + 2); i++) {
                    credits[i] = creditsLOW + i;
                }
            } else {
                credits = new double[1];
                credits[0] = creditsLOW;
            }
            
            String[] prerequisiteArray; 

            if (prerequisites != null) {
                prerequisiteArray = prerequisites.split(",");
            } else {
                prerequisiteArray = new String[0];
            }

            String[] corequisiteArray; 

            if (corequisites != null) {
                corequisiteArray = corequisites.split(",");
            } else {
                corequisiteArray = new String[0];
            }

            FullCourse fullCourse = new FullCourse(
                code, name, credits, description, prerequisiteArray, corequisiteArray, cdf, schedule, others);

            masterList.add(fullCourse);
        }        
        Gson gson = new Gson();
        gson.toJson(masterList);
        try {
            File file = new File("app/src/main/java/org/DataCollection/DataCache", "nocache.json");
            FileWriter writer = new FileWriter(file);
            gson.toJson(masterList, writer);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write Json file");
        }
    }

    public static void createJsonFromCache(String courseFileName, String gradeFileName) {
        File courseFile = new File("app/src/main/java/org/DataCollection/DataCache", courseFileName);
        File gradeFile = new File("app/src/main/java/org/DataCollection/DataCache", gradeFileName);

        if (!courseFile.exists() || !gradeFile.exists()) {
            throw new RuntimeException("\nThe specified caches do not exist");
        }

        List<Course> courseList = readCourses(courseFile);
        Map<String, Double> gradeMap = readGrades(gradeFile);

        createJson(courseList, gradeMap);
    }

    public static void createJsonAndCache(String courseFileName, String gradeFileName) {
        List<Course> courseList = WebScraper.getAllCourses();
        Map<String, Double> gradeMap = WebScraper.getAllGrades();

        cacheCourses(courseFileName, courseList);
        cacheGrades(gradeFileName, gradeMap);

        createJson(courseList, gradeMap);
    }

    public static void createJsonNoCache() {
        List<Course> courseList = WebScraper.getAllCourses();
        Map<String, Double> gradeMap = WebScraper.getAllGrades();

        createJson(courseList, gradeMap);
    }

    public static void cacheCourses(String outputFileName, List<Course> courseList) {
        File file = new File("app/src/main/java/org/DataCollection/DataCache", outputFileName + ".txt");
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

    public static void cacheGrades(String outputFileName, Map<String, Double> gradeMap) {
        File file = new File("app/src/main/java/org/DataCollection/DataCache", outputFileName + ".csv");
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

    public static List<Course> readCourses(File file) {

        List<Course> courseList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Title: ")) {
                    String title = line.substring(7); // Remove "Title: " prefix
                    String description = reader.readLine().substring(13); // Remove "Description: " prefix
                    courseList.add(new Course(title, description));
                }
            }
            reader.close();

        } catch (IOException e) {
            throw new RuntimeException("There was an error reading the file");
        }

        return courseList;
    }

    public static Map<String, Double> readGrades(File file) {

        Map<String, Double> gradeMap = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] temp = line.split(",");
                gradeMap.put(temp[0], Double.parseDouble(temp[1]));
            }
            reader.close();

        } catch (IOException e) {
            throw new RuntimeException("There was an error reading the file");
        }

        return gradeMap;
    }
}
