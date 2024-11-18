package org.DataCollection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private static final int COLOR_VALUE = 33;

    private static final int LENGTH_THRESHOLD = 6;

    public static void main(String[] args) {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
        
        createJsonFromCache("courses.txt", "grades.csv");
    }

    record FullCourse(
        String code,
        String name,
        double[] credits,
        String description,
        String[] prerequisites,
        String[] corequisites,
        boolean cdf,
        Schedule schedule,
        Others others
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
        Pattern preReqPattern = Pattern.compile("(?:Pre-?requisites?:(.*?(?=[A-Z]{2}))?([A-Z]+? \\d++[^.]*))");
        Pattern coReqPattern = Pattern.compile("(?:Co-?requisites?:(.*?(?=[A-Z]{2}))?([A-Z]+? \\d++[^.]*))");
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
            boolean cdf = false;
            double grade = -1;

            Matcher titleMatcher = titlePattern.matcher(c.Title());
            if (titleMatcher.find()) {
                code = titleMatcher.group(1) + "-" + titleMatcher.group(INDEX_VAL2); // Find course code
                creditsLOW = Double.parseDouble(titleMatcher.group(INDEX_VAL3)); // Find credits/lowerbound
                if (titleMatcher.group(INDEX_VAL4) != null) {
                    creditsHIGH = Double.parseDouble(titleMatcher.group(INDEX_VAL4)); // Find upperbound if it exists
                }
                name = titleMatcher.group(INDEX_VAL5).trim(); // Find course name
            }

            String desc = c.Description();

            description = desc.split("Pre-?requisites?:|Co-?requisites?:|\\[")[0]; // Find description
            
            double[] credits;
            if (creditsHIGH != -1) { // Find credits Array
                credits = new double[(int) (creditsHIGH - creditsLOW + 2)];
                for (int i = 0; i < credits.length; i++) {
                    credits[i] = creditsLOW + i;
                }
            } else {
                credits = new double[1];
                credits[0] = creditsLOW;
            }

            String[] prerequisiteArray = createCourseList(preReqPattern, desc);
            String[] corequisiteArray = createCourseList(coReqPattern, desc);

            if (desc.contains("This course is not eligible for Credit/D/Fail grading.")) {
                cdf = false;
            } 

            Schedule schedule = createSchedule(desc);

            if (gradeMap.containsKey(code)) {
                grade = gradeMap.get(code);
            }

            Others others = new Others(grade);

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

    private static Schedule createSchedule(String desc) {
        Pattern schedulePattern = Pattern.compile("\\[(\\d)(\\*?)-(\\d)(\\*?)-(\\d)(\\*?)\\]");
        Matcher scheduleMatcher = schedulePattern.matcher(desc);
        int lectures = -1;
        boolean alternating1 = false;
        int labs = -1;
        boolean alternating2 = false;
        int tutorials = -1;
        boolean alternating3 = false;

        if (scheduleMatcher.find()) {
            lectures = Integer.parseInt(scheduleMatcher.group(INDEX_VAL1));
            alternating1 = scheduleMatcher.group(INDEX_VAL2) != null && scheduleMatcher.group(INDEX_VAL2).contains("*");
            labs = Integer.parseInt(scheduleMatcher.group(INDEX_VAL3));
            alternating2 = scheduleMatcher.group(INDEX_VAL4) != null && scheduleMatcher.group(INDEX_VAL4).contains("*");
            tutorials = Integer.parseInt(scheduleMatcher.group(INDEX_VAL5));
            alternating3 = scheduleMatcher.group(INDEX_VAL6) != null && scheduleMatcher.group(INDEX_VAL6).contains("*");
        }

        return new Schedule(lectures, alternating1, labs, alternating2, tutorials, alternating3);
    }

    private static String[] createCourseList(Pattern coursePattern, String desc) {
        Matcher matcher = coursePattern.matcher(desc);
        String courses = null;
        String[] courseArray; 

        if (matcher.find() && matcher.group(1) != null) {
            courses = matcher.group(2).replaceAll("([A-Z]+) (\\d+)", "$1-$2");
            courses = courses.replaceAll(" ", ",");
            courses = courses.replaceAll("[^A-Z\\d,-]", "");
            courses = courses.replaceAll(",+", ",").trim();
        } 
        if (courses != null) {
            courseArray = courses.split(",");
        } else {
            courseArray = new String[0];
        }

        Set<String> validCourses = new HashSet<>();
        for (String course : courseArray) {
            if (course.length() >= LENGTH_THRESHOLD) {
                validCourses.add(course);
            }
        }

        return validCourses.toArray(new String[0]);
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
                writer.write(c.Title());
                writer.newLine();
                writer.write(c.Description());
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

    /**
     * Reads a text file and returns a List of <code>Course</code> objects.
     * The text file should contain Titles and Descriptions of courses as
     * Strings, each on a new line with courses and their corresponding
     * descriptions on alternating lines.
     *
     * @param file the file to read
     * @return a list of <code>Course</code> objects
     * @throws RuntimeException if there is an error reading the file
     */
    public static List<Course> readCourses(File file) {

        List<Course> courseList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String title = line;
                String desc = reader.readLine();
                courseList.add(new Course(title, desc));
            }
            reader.close();

        } catch (IOException e) {
            throw new RuntimeException("There was an error reading the file");
        }

        return courseList;
    }

    /**
     * Reads a CSV file and returns a map of the course codes to their corresponding grade.
     * The CSV file should have a header row with the columns labeled "Course" and "Grade".
     * Each subsequent row should have the course code and the grade for that course. Course
     * values must be Strings and Grade values must be doubles.
     * 
     * @param file the CSV file to read
     * @return a map of course codes to grades
     * @throws RuntimeException if there is an error reading the file
     */
    public static Map<String, Double> readGrades(File file) {

        Map<String, Double> gradeMap = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine(); // Gets rid of first line

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
