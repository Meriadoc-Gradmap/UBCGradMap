package org.GraphAPI;

import com.google.gson.Gson;
import org.Graph.Hours;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api")
public class Controller {
    record Others(double average, String professor) {
    }

    record courseFormat(String code,
            String name,
            int[] credits,
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
     *   "credits": [4, 5],
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

        int[] credits = { 4, 5 };
        String[] prerequisites = { "APSC-160" };
        String[] postrequisites = { "CPEN-212", "CPEN-322", "CPEN-422" };
        String[] corequisites = {};

        Hours hours = new Hours(3, false, 2, false, 2, true);
        Gson gson = new Gson();
        String hoursJson = gson.toJson(hours);

        StringBuilder courseJSON = new StringBuilder();
        // TODO: change this thing lol
        courseJSON.append("{");
        courseJSON.append(String.format("\"code\": \"%s\",", course));
        courseJSON.append(String.format("\"name\": \"%s\",", "Software Construction I"));
        courseJSON.append(String.format("\"credits\": %s,", Arrays.toString(credits)));
        courseJSON.append(String.format("\"prerequisites\": %s,", gson.toJson(prerequisites)));
        courseJSON.append(String.format("\"postrequisites\": %s,", gson.toJson(postrequisites)));
        courseJSON.append(String.format("\"corequisites\": %s,", gson.toJson(corequisites)));
        courseJSON.append(String.format("\"cdf\": %b,", false));
        courseJSON.append(String.format("\"schedule\": %s,", hoursJson));
        courseJSON.append("\"others\": {");
        courseJSON.append(String.format("\"average\": %d,", 87));
        courseJSON.append(String.format("\"professor\": \"%s\",", "Satish Gopalakrishnan"));
        courseJSON.append("}");
        courseJSON.append("}");
        return courseJSON.toString();
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

    @GetMapping("/getallcourses")
    @ResponseBody
    public String getAllCourses() {
        return "{\"Error getting courses (Iain has not implemented this method what a loser ong)\"}";
    }
}
