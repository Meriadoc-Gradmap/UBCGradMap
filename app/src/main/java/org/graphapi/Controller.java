package org.graphapi;

import com.google.gson.Gson;
import org.graph.Hours;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
/**
 * @author Iain Griesdale
 *         Provides api methods to interface with the backend graph of courses.
 *         These methods include
 *
 *         <ul>
 *         <li>{@code getTest} is a basic method to test that the api is online and can be accessed </li>
 *         <li>{@code getCourse} gets all required information for a specified course in json format</li>
 *         <li>{@code getAllCourses} gets a list of all the courses in json format</li>>
 *         </ul>
 *
 */
@RestController
@RequestMapping("/api")
public class Controller {
    record Others(double average, String professor) {
    }

    record CourseFormat(
            String code,
            String name,
            double[] credits,
            String description,
            String[] prerequisites,
            String[] postrequisites,
            String[] corequisites,
            boolean cdf,
            Hours schedule,
            Others others) {
    }

    /**
     * A test method to confirm the API is online
     *
     * @return a constant String
     */
    @GetMapping("/test")
    public String getTest() {
        return "Finn smells teehee~";
    }

    /**
     * Generates a course and its information in JSON format as follows:
     * 
     * <pre>
     * {
     *   "code": "CPEN-221",
     *   "name": "Course name",
     *   "credits": [4,5, 5.0],
     *   "description": "Course description",
     *   "prerequisites": ["APSC-160"],
     *   "postrequisites": ["CPEN-212", "CPEN-322", "CPEN-422"],
     *   "corequisites": [],
     *   "cdf": false,
     *   "schedule": {
     *     "lectures": 3,
     *     "alternating1": false,
     *     "labs": 2,
     *     "alternating2": false,
     *     "tutorials": 2,
     *     "alternating3": true,
     *   }
     *   "others": {
     *     "average": 87.0,
     *     "professor": "Satish Gopalakrishnan"
     *   }
     * }
     * </pre>
     * 
     * @param course is the code of the course to get.
     * @return the json formatted course information
     *
     * @throws IllegalArgumentException if the course format is incorrect or
     *                                  if the course cannot be found.
     */
    @GetMapping("/getcourse")
    @ResponseBody
    public String getCourse(@RequestParam String course) {
        if (!isValidCode(course))
            return "ERROR: Invalid course code";
        double[] credits = { 4.5, 5 };
        String[] prerequisites = { "APSC-160" };
        String[] postrequisites = { "CPEN-212", "CPEN-322", "CPEN-422" };
        String[] corequisites = {};

        Hours hoursTemp = new Hours(3, false, 2, false, 2, true);
        Others othersTemp = new Others(87.0, "Sathish Gopalakrishnan");
        Gson gson = new Gson();
        CourseFormat courseFormat = new CourseFormat("CPEN-221",
                "Software Construction I",
                credits,
                "Software Design blah blah blah",
                prerequisites,
                postrequisites,
                corequisites,
                false,
                hoursTemp,
                othersTemp);

        return gson.toJson(courseFormat);
    }

    /**
     * Checks if a given course code is a valid course code. A course code
     * is valid if it contains 3 or 4 letters followed by a dash, followed
     * by 3 numbers between 0 and 9.
     *
     * @param courseCode is the course code to check
     * @retun true if it is valid, false otherwise
     */
    private boolean isValidCode(String courseCode) {
        return courseCode.toUpperCase().matches("[A-Z]{3,4}?-[0-9]{3}");
    }

    /**
     * Gets all the course codes contained in the graph of courses.
     *
     * @return All the course codes contained in the grpah
     */
    @GetMapping("/getallcourses")
    @ResponseBody
    public String getAllCourses() {
        List<String> courses = new LinkedList<>();
        courses.add("CPEN-221");
        courses.add("FINN-101");
        Gson gson = new Gson();
        return gson.toJson(courses);
    }
}
