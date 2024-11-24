package org.datacollection;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class WebScraperTests {


    @Test
    void getDepartments (){
        System.out.println(WebScraper.getDepartments());

        String departmentExpected = null;
        try{
            File testFile = new File("src/test/java/org/files/departments.txt");
            System.out.println(testFile.getAbsolutePath());
            Scanner fileReader = new Scanner(testFile);
            departmentExpected = fileReader.nextLine(); // there should only be one line

        } catch (FileNotFoundException e) {
            System.out.println("Wrong Link On Testing File for getDepartments Test");
        }

        assertNotNull(departmentExpected);
        assertEquals(departmentExpected, WebScraper.getDepartments().toString());
    }

    @Test void getCoursesByDepartTest() {
        List<WebScraper.CourseRecord> math_courses = (WebScraper.getCoursesByDepartmentCode("MATH"));
        // first course should be math 100
        WebScraper.CourseRecord math100 = math_courses.get(0);
        assertEquals("MATH_V 100 (3) Differential Calculus with Applications", math100.Title());

        // test invalid
        assertThrows(RuntimeException.class, () -> WebScraper.getCoursesByDepartmentCode("This is a super invalid code"));
        assertEquals(Collections.emptyList(),WebScraper.getCoursesByDepartmentCode(null));
        assertEquals(Collections.emptyList(),WebScraper.getCoursesByDepartmentCode(""));

    }

    @Test void getGradesByDepartTest(){
        Map<String, Double> gradeMap = WebScraper.getGradesByDepartment("MATH");
        assertEquals(68.31, gradeMap.get("MATH-100"));
    }

    @Test void getCoursesByDepartInvalid() {
        assertEquals(Collections.emptyList(), WebScraper.getCoursesByDepartment(""));
    }
}
