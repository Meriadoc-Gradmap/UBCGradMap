package org.GraphAPI;

import org.springframework.web.bind.annotation.*;

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
        String courseJson = "{\"" + course + "\": {\"description\": \"I am a description\", \"courseName\": \"Verilog and shit\", \"credits\": 69}}";

        return courseJson;
    }

    @GetMapping("/getallcourses")
    @ResponseBody
    public String getAllCourses() {
        return "{\"Error getting courses (Iain has not implemented this method what a loser ong)\"}";
    }
}
