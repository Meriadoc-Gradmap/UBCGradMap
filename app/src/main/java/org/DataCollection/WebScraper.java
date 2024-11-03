package org.DataCollection;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.stream.Collectors;
import org.jsoup.select.Elements;

public class WebScraper {

    record Course(
            String Title,
            String description) {
    }
    
    public static void main(String[] args) {
        
        getDepartments("https://vancouver.calendar.ubc.ca/course-descriptions/courses-subject");

        // TODO: Add timer so i dont get my ip banned
        getCoursesByDepartment("https://vancouver.calendar.ubc.ca/course-descriptions/subject/cpenv");

    }

    public static List<String> getDepartments(String url) {
        Document doc; 
        try { 
            doc = Jsoup.connect(url).get(); 
        } catch (IOException e) { 
            throw new RuntimeException(e); 
        }

        Elements departmentElements = doc.getElementsByTag("li");

        List<String> urlList = departmentElements.stream().
            filter(e -> e.select("a").first() != null).
            filter(e -> e.select("a").first().attr("href").
            contains("https://vancouver.calendar.ubc.ca/course-descriptions/subject/")).
            map(e -> e.select("a").first().attr("href")).
            collect(Collectors.toList());
 
        return urlList;
    }

    public static List<Course> getCoursesByDepartment(String url) {
        Document doc; 
        try { 
            doc = Jsoup.connect(url).get(); 
        } catch (IOException e) { 
            throw new RuntimeException(e); 
        }

        Elements departmentElements = doc.getElementsByTag("li");
        List<Course> courseList = departmentElements.stream().
            filter(e -> e.select("h3").first() != null).
            filter(e -> e.select("p").first() != null).
            map(e -> new Course(e.select("h3").first().text(), e.select("p").first().text())).
            collect(Collectors.toList());

        return courseList;
    }
}
