package org.datacollection;

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

import org.datacollection.WebScraper.*;
import org.datacollection.WebScraper.CourseRecord;

import com.google.gson.Gson;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;


/**
 * DataFormatter contains methods used for the creation and 
 * storage of Formatted Course JSONs and caches containing
 * Course and grade information.
 * 
 * @author Tian Chen
 */
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
        /* This main method is what runs when someone passes a flag to scrape data from the docker
         * container. So we just create the json with no cache. Do not assume there is a cache.
         */
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        // createJsonFromCache("courses", "grades");
        createJsonAndCache("courses", "grades");

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

    /**
     * Others is declared as its own record in case we choose to add other info
     */
    record Others(
        double grade
    ) {
    }

    /**
     * Creates a JSON file containing all the information from the given list of courses and map of grades.
     * The JSON file is stored in the "data" directory with the name "COURSE_INFO.json". Read the file
     * "JSON Format.md" for information on the output. The JSON will be written to 
     * <code>CPEN 221\GradMap\project-meriadoc-gradmap\data</code>
     * and the file will be named <code>COURSE_INFO.json</code>.
     * 
     * @param courseList a list of Course objects
     * @param gradeMap a map of course codes to grades
     * @throws RuntimeException if there is an issue writing to the JSON
     */
    public static void createJson(List<CourseRecord> courseList, Map<String, Double> gradeMap) {
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

        for (CourseRecord c : ProgressBar.wrap(courseList, pbb)) {
            String code = "";
            double creditsLOW = -1;
            double creditsHIGH = -1;
            String name = "";
            boolean cdf = true;
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
            String description = getDescription(desc);


            double[] credits;
            if (creditsHIGH != -1) { // Find credits Array
                credits = new double[2];
                credits[0] = creditsLOW;
                credits[1] = creditsHIGH;
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
            File file = new File("../data", "COURSE_INFO.json");
            FileWriter writer = new FileWriter(file);
            gson.toJson(masterList, writer);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write Json file");
        }
    }

    private static String getDescription(String desc) {
        String description = desc.replaceAll("\\[[^\\[\\]]*]", ""); // Remove hours
        description = description.replaceAll("This course is (not )?eligible for Credit/D/Fail grading\\.",
                ""); // Remove cdf
        description = description.replaceAll(
                "(?i)(?:Consult|See|Please) [A-Za-z\\s]+ [^�]*�[^ ]*", "");
        description = description.replaceAll(
                " {3}", " ");
        return description;
    }

    /**
     * Creates a Schedule object based on the course description passed in.
     * The description is expected to contain a pattern of the form:
     * [1-1-1*] where lectures, labs, and tutorials are integers, and
     * alternating1, alternating2, and alternating3 are either empty or "*".
     * If the description does not contain schedule information, values of
     * the array will be -1 or false.
     * 
     * @param desc the course description to parse
     * @return a Schedule object
     */
    private static Schedule createSchedule(String desc) {
        Pattern schedulePattern = Pattern.compile("\\[(\\d)?(\\*?)-?(\\d)?(\\*?)-?(\\d)?(\\*?)\\]");
        Matcher scheduleMatcher = schedulePattern.matcher(desc);
        int lectures = -1;
        boolean alternating1 = false;
        int labs = -1;
        boolean alternating2 = false;
        int tutorials = -1;
        boolean alternating3 = false;

        if (scheduleMatcher.find()) {
            if (scheduleMatcher.group(INDEX_VAL1) != null) {
                lectures = Integer.parseInt(scheduleMatcher.group(INDEX_VAL1));
            }
            alternating1 = scheduleMatcher.group(INDEX_VAL2) != null && scheduleMatcher.group(INDEX_VAL2).contains("*");
            if (scheduleMatcher.group(INDEX_VAL3) != null) {
                labs = Integer.parseInt(scheduleMatcher.group(INDEX_VAL3));
            }
            alternating2 = scheduleMatcher.group(INDEX_VAL4) != null && scheduleMatcher.group(INDEX_VAL4).contains("*");
            if (scheduleMatcher.group(INDEX_VAL5) != null) {
                tutorials = Integer.parseInt(scheduleMatcher.group(INDEX_VAL5));
            }
            alternating3 = scheduleMatcher.group(INDEX_VAL6) != null && scheduleMatcher.group(INDEX_VAL6).contains("*");
        }

        return new Schedule(lectures, alternating1, labs, alternating2, tutorials, alternating3);
    }

    /**
     * Given a pattern and a description, returns an array of courses that exist in the description
     * The courses are formatted as "CPEN-221" and the array is filtered to only include courses that
     * have a length above a certain threshold to avoid false positives. This threshold is based on
     * the minimum length of UBC course codes, which is 6. Users should take note that due to UBC
     * Calendar's non standard course descriptions, this method cannot guarantee 100% success and false
     * positves may be present in outputs (For example, a string such as "NUMBER-001" could be falsely
     * interpreted as a course code). Users should take note to check all outputs. If there are no courses
     * in the provided description or the provided pattern is incorrect, the method returns an empty
     * String Array.
     * 
     * @param coursePattern the pattern to match against the description
     * @param desc the description to match against
     * @return an array of courses that exist in the description
     */
    private static String[] createCourseList(Pattern coursePattern, String desc) {
        Matcher matcher = coursePattern.matcher(desc);
        String courses = null;
        String[] courseArray; 

        if (matcher.find() && matcher.group(1) != null) {
            courses = matcher.group(2).replaceAll("([A-Z]+) (\\d+)", "$1-$2");
            courses = courses.replaceAll(" ", ",");
            courses = courses.replaceAll("[^A-Z\\d,-]", "");
            courses = courses.replaceAll(",+", ",").trim();
        } else {
            return new String[0];
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

    /**
     * Creates a JSON file from the given cached course and grade data. Input file names
     * should not contain their file type. The JSON will be written to 
     * <code>CPEN 221\GradMap\project-meriadoc-gradmap\data</code>
     * and the file will be named <code>COURSE_INFO.json</code>.
     * 
     * @param courseFileName The name of the cache file containing all the courses.
     * @param gradeFileName The name of the cache file containing all the grades.
     */
    public static void createJsonFromCache(String courseFileName, String gradeFileName) {
        File courseFile = new File("src/main/java/org/datacollection/DataCache", courseFileName + ".txt");
        File gradeFile = new File("src/main/java/org/datacollection/DataCache", gradeFileName + ".csv");

        if (!courseFile.exists() || !gradeFile.exists()) {
            throw new RuntimeException("\nThe specified caches do not exist");
        }

        List<CourseRecord> courseList = readCourses(courseFile);
        Map<String, Double> gradeMap = readGrades(gradeFile);

        createJson(courseList, gradeMap);
    }

    /**
     * Fetches all courses and their corresponding grades using the WebScraper, 
     * caches all retrieved data andcreates a JSON representation of this data.
     * Input file names should not contain their file type.The JSON will be written
     * to <code>CPEN 221\GradMap\project-meriadoc-gradmap\data</code>
     * and the file will be named <code>COURSE_INFO.json</code>.
     * 
     * @param courseFileName The name of the cache file containing all the courses.
     * @param gradeFileName The name of the cache file containing all the grades.
     */
    public static void createJsonAndCache(String courseFileName, String gradeFileName) {
        List<CourseRecord> courseList = WebScraper.getAllCourses();
        Map<String, Double> gradeMap = WebScraper.getAllGrades();

        cacheCourses(courseFileName, courseList);
        cacheGrades(gradeFileName, gradeMap);

        createJson(courseList, gradeMap);
    }

    /**
     * Fetches all courses and their corresponding grades using the WebScraper,
     * and creates a JSON representation of this data without caching it locally.
     * The JSON will be written to <code>CPEN 221\GradMap\project-meriadoc-gradmap\data</code>
     * and the file will be named <code>COURSE_INFO.json</code>.
     */
    public static void createJsonNoCache() {
        List<CourseRecord> courseList = WebScraper.getAllCourses();
        Map<String, Double> gradeMap = WebScraper.getAllGrades();

        createJson(courseList, gradeMap);
    }

    /**
     * Writes a given List of courses to a txt file in the DataCache directory.
     * The List should contain <code>Course</code> Records. Courses will be stored
     * with Course titles and corresponding descriptions on alternating lines
     * All caches will be written to the relative path: 
     * <code>app/src/main/java/org/DataCollection/DataCache</code>
     *
     * @param outputFileName the name of the output file
     * @param gradeMap the map of grades to be cached
     * @throws RuntimeException if there is an error writing to the file
     */
    public static void cacheCourses(String outputFileName, List<CourseRecord> courseList) {
        File file = new File("src/main/java/org/datacollection/DataCache", outputFileName + ".txt");
        try {
            FileWriter fwrite = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fwrite);
            for (CourseRecord c : courseList) {
                writer.write(c.Title());
                writer.newLine();
                writer.write(c.Description());
                writer.newLine();
            }
            writer.close();
            fwrite.close();
        } catch (IOException e) {
            throw new RuntimeException("There was an error writing to the file");
        }
    }

    /**
     * Writes a given Map of grades to a csv file in the DataCache directory.
     * The map should contain the course code as the key and the average grade
     * as the value. All caches will be written to the relative path:
     * <code>app/src/main/java/org/DataCollection/DataCache</code>
     *
     * @param outputFileName the name of the output file
     * @param gradeMap the map of grades to be cached
     * @throws RuntimeException if there is an error writing to the file
     */
    public static void cacheGrades(String outputFileName, Map<String, Double> gradeMap) {
        File file = new File("src/main/java/org/datacollection/DataCache", outputFileName + ".csv");
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
            throw new RuntimeException("There was an error writing to the file");
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
    public static List<CourseRecord> readCourses(File file) {

        List<CourseRecord> courseList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String title = line;
                String desc = reader.readLine();
                courseList.add(new CourseRecord(title, desc));
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
