package org.graphapi;

import org.GraphAPI.Controller;
import org.GraphAPI.GraphApi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ControllerTests {

    @Autowired
    private Controller controller;

    @Test
    void contextLoads(){
        assertNotNull(controller);
        System.out.println("SERVER RUNNING");
    }

    @Test
    public void isValidTest(){

    }

    @Test
    public void getCourseTest(){
        System.out.println(controller.getCourse("CPEN-221"));
    }
}
