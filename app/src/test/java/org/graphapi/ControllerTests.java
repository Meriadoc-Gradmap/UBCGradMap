package org.graphapi;

import org.graphapi.Controller;
import org.graphapi.GraphApi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ControllerTests {


    private final String cpen221 = "{\\\"code\\\":\\\"CPEN-221\\\",\\\"name\\\":\\\"Software Construction I\\\",\\\"credits\\\":[4.5,5.0],\\\"description\\\":\\\"Software Design blah blah blah\\\",\\\"prerequisites\\\":[\\\"APSC-160\\\"],\\\"postrequisites\\\":[\\\"CPEN-212\\\",\\\"CPEN-322\\\",\\\"CPEN-422\\\"],\\\"corequisites\\\":[],\\\"cdf\\\":false,\\\"schedule\\\":{\\\"lectures\\\":3,\\\"alternating1\\\":false,\\\"labs\\\":2,\\\"alternating2\\\":false,\\\"tutorials\\\":2,\\\"alternating3\\\":true},\\\"others\\\":{\\\"average\\\":87.0,\\\"professor\\\":\\\"Sathish Gopalakrishnan\\\"}}";

    @Autowired
    private Controller controller;

    @Test
    void contextLoads() {
        assertNotNull(controller);
        System.out.println("SERVER RUNNING");
    }

    @Test
    public void getCourseTest() {
        System.out.println(controller.getCourse("CPEN-221"));
        assertEquals(cpen221, controller.getCourse("CPEN-221"));
    }


}
