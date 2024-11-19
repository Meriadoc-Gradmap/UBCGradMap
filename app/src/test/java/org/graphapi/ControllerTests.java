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

    private final String cpen221 = "{\"code\":\"CPEN-221\",\"name\":\"Software Construction I\",\"credits\":[4.0,5.0,6.0],\"description\":\"Design, implementation, reasoning about software systems: abstraction and specification of software, testing, verification, abstract data types, object-oriented design, type hierarchies, concurrent software design. \",\"prerequisites\":[\"APSC-160\"],\"postrequisites\":[\"DSCI-310\",\"CPEN-331\",\"CPEN-441\",\"DSCI-320\",\"BMEG-310\",\"CPEN-212\"],\"corequisites\":[\"\"],\"cdf\":false,\"schedule\":{\"lectures\":3,\"alternating1\":false,\"labs\":2,\"alternating2\":false,\"tutorials\":2,\"alternating3\":true},\"others\":{\"grade\":73.22}}";

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
