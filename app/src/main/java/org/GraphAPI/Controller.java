package org.GraphAPI;

import com.google.gson.Gson;
import org.Graph.Hours;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api")
public class Controller {

    @GetMapping("/test")
    public String getTest() {
        return "Finn smells teehee~";
    }

    @GetMapping("/getcourse")
    @ResponseBody
    public String getCourse(@RequestParam String course) {
        int[] credits = {4, 5};
        String[] prerequisites = {"APSC-160"};
        String[] postrequisites = {"CPEN-212", "CPEN-322", "CPEN-422"};
        String[] corequisites = {};

        Hours hours = new Hours(3, false, 2, false, 2, true);
        Gson gson = new Gson();
        String hoursJson = gson.toJson(hours);


        StringBuilder courseJSON = new StringBuilder();
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

    @GetMapping("/getallcourses")
    @ResponseBody
    public String getAllCourses() {
        return "{\"Error getting courses (Iain has not implemented this method what a loser ong)\"}";
    }
}
