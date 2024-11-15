package org.DataCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.stream.Collectors;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import me.tongfei.progressbar.*;

public class WebScraper {

    private static final double ROUND_VALUE = 100.0;
    private static final int COLOR_VALUE = 33;


    record Course(
            String Title,
            String Description) {
    }
    
    public static void main(String[] args) {
        
        Map<String, Double> courseList = getAllGrades();

        for (String key : courseList.keySet()) {
            System.out.println(key);
            System.out.println(courseList.get(key));
        }

    }

    public static List<Course> getAllCourses() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
        List<Course> courseList = new ArrayList<>();
        List<String> departmentList = getDepartments();
        System.out.println("Fetching all Courses");
        long startTime = System.currentTimeMillis();

        ProgressBarBuilder pbb = ProgressBar.builder().
                        setStyle(ProgressBarStyle.builder().
                        colorCode((byte) COLOR_VALUE).
                        leftBracket("[").
                        rightBracket("]").
                        block('=').
                        rightSideFractionSymbol('>').
                        build()
                        );

        for (String s : ProgressBar.wrap(departmentList, pbb)) {
            courseList.addAll(getCoursesByDepartment(s));
        }

        System.out.println("Full Elapsed time: " + (System.currentTimeMillis() - startTime) + "ms \n");

        return courseList;
    }

    public static Map<String, Double> getAllGrades() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
        Map<String, Double> gradeMap = new HashMap<>();
        List<String> departmentList = getDepartments();
        System.out.println("Fetching all Grades");
        long startTime = System.currentTimeMillis();

        ProgressBarBuilder pbb = ProgressBar.builder().
                        setStyle(ProgressBarStyle.builder().
                        colorCode((byte) COLOR_VALUE).
                        leftBracket("[").
                        rightBracket("]").
                        block('=').
                        rightSideFractionSymbol('>').
                        build()
                        );

        for (String s : ProgressBar.wrap(departmentList, pbb)) {
            String code = s.substring(s.lastIndexOf("/") + 1, s.length() - 1);
            code = code.toUpperCase();
            gradeMap.putAll(getGradesByDepartment(code));
        }

        System.out.println("Full Elapsed time: " + (System.currentTimeMillis() - startTime) + "ms \n");

        return gradeMap;
    }

    /**
     * Fetches a list of department URLs from the UBC course descriptions page.
     * This method should be called before attempting to retrieve course data to
     * ensure most up-to-date department info is fetched.
     *
     * @return a list of URLs for each department's course descriptions
     * @throws RuntimeException if there is an error connecting to the URL.
     */
    public static List<String> getDepartments() {
        Document doc; 
        try { 
            doc = Jsoup.connect("https://vancouver.calendar.ubc.ca/course-descriptions/courses-subject").get(); 
        } catch (IOException e) { 
            throw new RuntimeException("There was an issue connecting to the UBC Calendar website."); 
        }

        Elements urlElement = doc.getElementsByAttributeValue("property", "og:url");

        Elements departmentElements = doc.getElementsByTag("li");

        List<String> urlList = departmentElements.stream().
            filter(e -> e.select("a").first() != null).
            filter(e -> e.select("a").first().attr("href").
            contains("https://vancouver.calendar.ubc.ca/course-descriptions/subject/")).
            map(e -> e.select("a").first().attr("href")).
            collect(Collectors.toList());

        return urlList;
    }

    /**
     * Fetches a list of courses from a department page on the UBC course descriptions website.
     * This URL must be from the UBC calendar website (i.e. it contains 
     * https://vancouver.calendar.ubc.ca/course-descriptions/subject/<DEPARTMENT-CODE-HERE>).
     * Otherwise, the method will return an empty List. If the deparment code in the URL is
     * incorrect, the method makes no guarantees about its output. Users should check their
     * URLs are valid before usage of this method.
     *
     * @param url the URL of the department page containing course descriptions. 
     * @return a list of courses, each containing the course title and description
     * @throws RuntimeException if there is an error connecting to the URL
     */
    public static List<Course> getCoursesByDepartment(String url) {
        Document doc; 
        Elements departmentElements;
        List<Course> courseList;

        if (!url.contains("https://vancouver.calendar.ubc.ca/course-descriptions/subject/")) {
            return Collections.emptyList();
        }

        try { 
            doc = Jsoup.connect(url).get(); 
        } catch (IOException e) { 
            throw new RuntimeException("There was an issue connecting to the UBC Calendar Website."); 
        }

        

        departmentElements = doc.getElementsByTag("li");
        courseList = departmentElements.stream().
            filter(e -> e.select("h3").first() != null).
            filter(e -> e.select("p").first() != null).
            map(e -> new Course(e.select("h3").first().text(), e.select("p").first().text())).
            collect(Collectors.toList());
        
        return courseList;
    }

    /**
     * Unlike <code>getCoursesByDepartment(String url)</code>, this method takes the course code
     * as its input. If the department code is null or empty, an empty List will be returned. If 
     * the department code is invalid, the program can make no guarantees about its output. Users
     * should check their codes are valid before usage of this method. For reference, most UBC
     * codes are 4 letters long and contain no special characters (Ex. "CPEN", "APSC", etc.)
     *
     * @param code the department code of the page containing course descriptions. The code must
     * be valid (i.e. UBC Vancouver has a department with this code)
     * @return a list of courses, each containing the course title and description
     * @throws RuntimeException if there is an error connecting to the URL
 */
    public static List<Course> getCoursesByDepartmentCode(String code) {
        if (code == null || code.isEmpty()) {
            return Collections.emptyList();
        }
        return getCoursesByDepartment("https://vancouver.calendar.ubc.ca/course-descriptions/subject/" + code + "v");
    }

    /**
     * Retrieves a Mapof courses and their average grades from the UBCgrades API, given
     * a department code. The department code must be valid (i.e. UBC Vancouver has a 
     * department with this code). Otherwise, the method will return an empty map. If a
     * course does not have grade data, its average grade will be -1.0. (No legitimate course
     * should ever have a negative average). Since this method fetches data from a 3rd party
     * source, there are no guarantees about the accuracy of the data fetched.
     * 
     * @param department the department code of the courses to retrieve grades from
     * @return a Map of Strings of Course codes mapped to their average grades
     * @throws RuntimeException if there is an error connecting to the URL or parsing the data
     */
    public static Map<String, Double> getGradesByDepartment(String department) {
        String data;
        JsonArray gradeArray;
        String title;
        Double grade;
        Map<String, Double> departmentMap = new HashMap<>();

        try { 
            data = Jsoup.connect("https://ubcgrades.com/api/v3/course-statistics/UBCV/" + department).
                ignoreContentType(true).execute().body();
        } catch (IOException e) { 
            return departmentMap;
        }

        try {
            gradeArray = JsonParser.parseString(data).getAsJsonArray();
        } catch (JsonParseException e) {
            throw new RuntimeException("There was an issue parsing the data from the UBCgrades API");
        }
        
        for (JsonElement j : gradeArray) {
            JsonObject jObj = j.getAsJsonObject();
            title = jObj.get("subject").getAsString() + "-" + jObj.get("course").getAsString();
            if (jObj.get("average").getAsString().equals("")) {
                grade = -1.0;
            } else {
                grade = Math.round(jObj.get("average").getAsDouble() * ROUND_VALUE) / ROUND_VALUE;
            }
            departmentMap.put(title, grade);
        }

        return departmentMap;
    }
}
