package org.graphapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Spring Boot Controller
 * @author William Banquier
 */
@SpringBootTest(classes = Controller.class)
public class ControllerTests {

    @Autowired
    private Controller controller;


    @Test
    void contextLoads() {
        assertNotNull(controller);
        System.out.println("SERVER RUNNING");
        assertEquals("200", controller.getTest());
    }

    private final String cpen221 = "{\"code\":\"CPEN-221\",\"name\":\"Software Construction I\",\"credits\":[4.0,5.0],\"description\":\"Design, implementation, reasoning about software systems: abstraction and specification of software, testing, verification, abstract data types, object-oriented design, type hierarchies, concurrent software design.  Prerequisite: APSC 160. \",\"prerequisites\":[\"APSC-160\"],\"postrequisites\":[\"CPEN-416\",\"ENPH-353\",\"CPSC-330\",\"CPSC-221\",\"CPSC-344\",\"CPSC-312\",\"CPSC-302\",\"CPSC-368\",\"CPSC-303\",\"CPSC-261\",\"DSCI-310\",\"CPEN-320\",\"CPEN-331\",\"CPEN-441\",\"DSCI-320\",\"BMEG-310\",\"CPEN-431\",\"CPEN-212\",\"CPEN-355\",\"CPEN-422\",\"CPEN-455\",\"CPEN-423\"],\"corequisites\":[],\"cdf\":false,\"schedule\":{\"lectures\":3,\"alternating1\":false,\"labs\":2,\"alternating2\":false,\"tutorials\":2,\"alternating3\":true},\"others\":{\"grade\":73.22}}";
    @Test
    public void getCourseTest() {
        assertEquals(cpen221, controller.getCourse("CPEN-221"));
    }

    @Test void invalidCourseTest(){
       assertEquals("\"ERROR: High school course\"", controller.getCourse("MATH-12"));
       assertEquals("\"ERROR: Invalid course code\"", controller.getCourse("INVALID_COURSE"));
       assertEquals("\"ERROR: No course found\"", controller.getCourse("ABCD-999"));
    }

    @Test void getAllCourses(){
//        System.out.println(controller.getAllCourses());
        String coursesActual = controller.getAllCourses();
        String coursesExpected = null;
        try{
            File testFile = new File("src/test/java/org/files/allcourses.txt");
            System.out.println(testFile.getAbsolutePath());
            Scanner fileReader = new Scanner(testFile);
            coursesExpected = fileReader.nextLine(); // there should only be one line

        } catch (FileNotFoundException e) {
            System.out.println("Wrong Link On Testing File for getAllCourses Test");
        }

        assertNotNull(coursesExpected);
        assertEquals(coursesExpected, coursesActual);
    }



}
