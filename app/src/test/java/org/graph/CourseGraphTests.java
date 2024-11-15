package org.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourseGraphTests {

    double[] acred;
    double[] bcred;
    double[] ccred;

    Set<String> aPreq;
    Set<String> aCo;
    Set<String> bPreq;
    Set<String> bCo;
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

        acred = new double[] {1,2};
        bcred = new double[] {3};
        ccred = new double[] {112}; // yep it's a lot of credits

        aPreq = new HashSet<>();
        bPreq = new HashSet<>();
        aCo = new HashSet<>();
        bCo = new HashSet<>();
        aCo.add("b");
        bCo.add("a");

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
                aCo,
                false,
                aHours,
                aOther,
                0);
        b = new Course(
                "b",
                "course b",
                bcred,
                "desc b",
                bPreq,
                bCo,
                false,
                bHours,
                bOther,
                1);
        c = new Course(
                "c",
                "course c",
                ccred,
                "desc c",
                cPreq,
                cPost,
                false,
                cHours,
                cOther,
                2);

        courseSet = new HashSet<>();
        courseSet.add(a);
        courseSet.add(b);
        courseSet.add(c);
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
