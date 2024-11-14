package org.DataCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.stream.Collectors;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class WebScraper {

    record Course(
            String Title,
            String Description) {
    }

    record Grade(
            String Title,
            Double Grade) {
    }
    
    public static void main(String[] args) {
        
        List<Course> courseList = getAllCourses();

        for (Course c : courseList) {
            System.out.println("Title: " + c.Title());
            System.out.println("Description: " + c.Description());
            System.out.println();
        }

    }

    public static List<Course> getAllCourses() {
        List<Course> courseList = new ArrayList<>();
        List<String> departmentList = getDepartments();
        System.out.println("Fetching all Courses");

        for (String s : departmentList) {

            System.out.println("Beginning to fetch: " + s.substring(s.lastIndexOf("/") + 1));
            long time1 = System.currentTimeMillis();
            
            courseList.addAll(getCoursesByDepartment(s));

            long time2 = System.currentTimeMillis();
            System.out.println("Finished fetching: " + s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf("v")));
            System.out.println("Elapsed time: " + (time2 - time1) + "ms \n");
        }
        return courseList;
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
            throw new RuntimeException("There was an issue connecting to the UBC Calendar website. Check your internet connection"); 
        }

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
            throw new RuntimeException
            ("There was an issue connecting to the UBC Calendar Website. Check your internet connection and that the provided URL is correct"); 
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
     * Retrieves a list of courses and their average grades from the UBCgrades API, given
     * a department code. The department code must be valid (i.e. UBC Vancouver has a 
     * department with this code). Otherwise, the method will return an empty list. If a
     * course does not have grade data, its average grade will be -1.0. (No legitimate course
     * should ever have a negative average). Since this method fetches data from a 3rd party
     * source, there are no guarantees about the accuracy of the data fetched.
     * 
     * @param department the department code of the courses to retrieve grades from
     * @return a list of courses, each containing the course title and average grade
     * @throws RuntimeException if there is an error connecting to the URL or parsing the data
     */
    public static List<Grade> getGradesByDepartment(String department) {
        String data;
        JsonArray gradeArray;
        String title;
        Double grade;
        List<Grade> departmentList = new ArrayList<>();;

        try { 
            data = Jsoup.connect("https://ubcgrades.com/api/v3/course-statistics/UBCV/" + department).
                ignoreContentType(true).execute().body();
        } catch (IOException e) { 
            throw new RuntimeException
                ("There was an issue connecting to the UBCgrades API. Check your internet connection and that the department code is correct"); 
        }

        try {
            gradeArray = JsonParser.parseString(data).getAsJsonArray();
        } catch (JsonParseException e){
            throw new RuntimeException("There was an issue parsing the data from the UBCgrades API");
        }
        
        for (JsonElement j : gradeArray) {
            JsonObject jObj = j.getAsJsonObject();
            title = jObj.get("subject").getAsString() + "-" + jObj.get("course").getAsString();;
            if (jObj.get("average").getAsString().equals("")) {
                grade = -1.0;
            } else {
                grade = Math.round(jObj.get("average").getAsDouble() * 100.0) / 100.0;
            }
            departmentList.add(new Grade(title, grade));
            System.out.println(title);
            System.out.println(grade);
            System.out.println("");
        }

        return departmentList;
    }
}
