package org.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourseGraphTests {

    int[] acred;
    int[] bcred;
    int[] ccred;

    Set<String> aPreq;
    Set<String> aPost;
    Set<String> bPreq;
    Set<String> bPost;
    Set<String> cPreq;
    Set<String> cPost;

    Hours aHours;
    Hours bHours;
    Hours cHours;

    Others aOther;
    Others bOther;
    Others cOther;

    Course a;
    Course b;
    Course c;

    Set<Course> courseSet;

    @BeforeEach
    public void beforeEach(){
        // create three courses that link together
        // a and b are coreqs
        // together you can do c
        // a + b = c :-)

        acred = new int[] {1,2};
        bcred = new int[] {3};
        ccred = new int[] {112}; // yep it's a lot of credits

        aPreq = new HashSet<>();
        bPreq = new HashSet<>();
        aPost = new HashSet<>();
        bPost = new HashSet<>();
        aPost.add("c");
        bPost.add("c");
        aPreq.add("b");
        aPost.add("b");
        bPreq.add("a");
        bPost.add("a");

        cPreq = new HashSet<>();
        cPreq.add("a"); cPreq.add("b");
        cPost = new HashSet<>();

        aHours = new Hours(1,false, 1,false,1,false);
        bHours = new Hours(1,false, 1,false,1,false);
        cHours = new Hours(1,false, 1,false,1,false);

        aOther = new Others(0,"Dr. Prof");
        bOther = new Others(50,"Dr. Prof");
        cOther = new Others(10,"Dr. Prof");

        a = new Course(
                "a",
                "course a",
                acred,
                "desc a",
                aPreq,
                aPost,
                false,
                aHours,
                aOther);
        b = new Course(
                "b",
                "course b",
                bcred,
                "desc b",
                bPreq,
                bPost,
                false,
                bHours,
                bOther);
        c = new Course(
                "c",
                "course c",
                ccred,
                "desc c",
                cPreq,
                cPost,
                false,
                cHours,
                cOther);

        courseSet = new HashSet<>();
        courseSet.add(a);
        courseSet.add(b);
        courseSet.add(c);
    }

    @Test
    public void invalidCourseGraph(){//TODO: Test is an infinite loop now
        // will it notice if id < 0 or if id > graph size
//        while (a.id() != -42) // not the meaning of life
//            a.initId();
//        assertThrows(IllegalArgumentException.class, () -> new CourseGraph(courseSet));
//
//        while (a.id() != 42)
//            a.initId();
//        assertThrows(IllegalArgumentException.class, () -> new CourseGraph(courseSet));
    }

    @Test
    public void testCoReqs(){
        CourseGraph cg = new CourseGraph(courseSet);
        Set<String> coreqsofA = new HashSet<>();
        coreqsofA.add("b");
        assertEquals(coreqsofA, cg.getCoRequisites("a"));
        assertEquals(Set.of("a"), cg.getCoRequisites("b"));
    }

    @Test
    public void getCodes(){
        CourseGraph cg = new CourseGraph(courseSet);
        // these are the codes, not the names fyi
        String[] expectedNames = new String[] {"a","b","c"};
        String[] actualNames = cg.getCodes();

        assertEquals(new HashSet<>(List.of(expectedNames)), new HashSet<>(List.of(actualNames)));
    }

    @Test
    public void testGettingPreAndPost(){
        CourseGraph cg = new CourseGraph(courseSet);
        assertEquals(aPreq, cg.getPreRequisites("a"));
        assertEquals(bPreq, cg.getPreRequisites("b"));
        assertEquals(cPreq, cg.getPreRequisites("c"));

        assertEquals(aPost, cg.getPostRequisites("a"));
        assertEquals(bPost, cg.getPostRequisites("b"));
        assertEquals(cPost, cg.getPostRequisites("c"));
    }

    @Test
    public void getCourseTest(){
        CourseGraph cg = new CourseGraph(courseSet);
        assertEquals(a, cg.getCourse("a"));
        assertEquals(b, cg.getCourse("b"));
        assertEquals(c, cg.getCourse("c"));
    }
}
