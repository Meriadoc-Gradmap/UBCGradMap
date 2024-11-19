package org.graphapi;

import com.google.gson.Gson;

import jakarta.annotation.PostConstruct;

import org.graph.CourseGraph;
import org.graph.Course;
import org.graph.GraphCreator;
import org.graph.Others;
import org.graph.Hours;
import org.springframework.web.bind.annotation.*;

/**
 * @author Iain Griesdale
 *         Provides api methods to interface with the backend graph of courses.
 *         These methods include
 *
 *         <ul>
 *         <li>{@code getTest} is a basic method to test that the api is online
 *         and can be accessed</li>
 *         <li>{@code getCourse} gets all required information for a specified
 *         course in json format</li>
 *         <li>{@code getAllCourses} gets a list of all the courses in json
 *         format</li>>
 *         </ul>
 *
 */
@RestController
@RequestMapping("/api")
public class Controller {
    private CourseGraph courseGraph;

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

    @PostConstruct
    public void initializeGraph() {
        this.courseGraph = GraphCreator.createGraph("data/COURSE_INFO.json");
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
     * @return the json formatted course information. Can return three
     *         error messages as follows
     *         <ul>
     *         <li>"ERROR: High school course" if the course is a high school pre
     *         requisite</li>
     *         <li>"ERROR: Invalid course code" if the course code is not a valid
     *         course format</li>
     *         <li>"ERROR: No course found" if the course is not in the
     *         graph/database</li>
     *         </ul>
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/getcourse")
    @ResponseBody
    public String getCourse(@RequestParam String course) {
        if (!isValidCode(course)) {
            if (isHighSchoolCourse(course)) {
                return "\"ERROR: High school course\"";
            }
            return "\"ERROR: Invalid course code\"";
        }
        String code = course.toUpperCase();
        try {
            Course courseNode = courseGraph.getCourse(code);
            Gson gson = new Gson();
            CourseFormat courseFormat = new CourseFormat(code,
                    courseNode.getName(),
                    courseNode.getCredits(),
                    courseNode.getDescription(),
                    courseNode.getPreRequisites().toArray(String[]::new),
                    courseGraph.getPostRequisites(code).toArray(String[]::new),
                    courseNode.getCorequisites().toArray(String[]::new),
                    courseNode.isCdf(),
                    courseNode.getWeeklyHours(),
                    courseNode.getOthers());

            return gson.toJson(courseFormat);
        } catch (IllegalArgumentException e) {
            return "\"ERROR: No course found\"";
        }
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
        return courseCode.toUpperCase().matches("[A-Z]{2,4}?-[0-9]{3}");
    }

    /**
     * Checks if a given course code is a High School Course. A high school
     * course has 4 letters followed by a dash and then and 1 and a number.
     *
     * @param courseCode is the course code to check
     * @retun true if it is a high school course
     */
    private boolean isHighSchoolCourse(String courseCode) {
        return courseCode.toUpperCase().matches("[A-Z]{4}-1[0-9]");
    }

    /**
     * Gets all the course codes contained in the graph of courses.
     *
     * @return All the course codes contained in the grpah
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/getallcourses")
    @ResponseBody
    public String getAllCourses() {
        String[] courses = courseGraph.getCodes();
        Gson gson = new Gson();
        return gson.toJson(courses);
    }
}
