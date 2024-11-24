package org.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourseRecordGraphTests {

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
    public void beforeEach() {
        // create three courses that link together
        // a and b are coreqs
        // together you can do c
        // a + b = c :-)

        acred = new double[] { 1, 2 };
        bcred = new double[] { 3 };
        ccred = new double[] { 112 }; // yep it's a lot of credits

        aPreq = new HashSet<>();
        bPreq = new HashSet<>();
        aCo = new HashSet<>();
        bCo = new HashSet<>();
        aCo.add("b");
        bCo.add("a");

        cPreq = new HashSet<>();
        cPreq.add("a");
        cPreq.add("b");

        aHours = new Hours(1, false, 1, false, 1, false);
        bHours = new Hours(1, false, 1, false, 1, false);
        cHours = new Hours(1, false, 1, false, 1, false);

        aOther = new Others(0);
        bOther = new Others(50);
        cOther = new Others(10);

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
                new HashSet<>(),
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
    public void testCoReqs() {
        CourseGraph cg = new CourseGraph(courseSet);
        Set<String> coreqsofA = new HashSet<>();
        coreqsofA.add("b");
        assertEquals(coreqsofA, cg.getCoRequisites("a"));
        assertEquals(Set.of("a"), cg.getCoRequisites("b"));
    }

    @Test
    public void getCodes() {
        CourseGraph cg = new CourseGraph(courseSet);
        // these are the codes, not the names fyi
        String[] expectedNames = new String[] { "a", "b", "c" };
        String[] actualNames = cg.getCodes();

        assertEquals(new HashSet<>(List.of(expectedNames)), new HashSet<>(List.of(actualNames)));
    }

    @Test
    public void testGettingPreAndPost() {
        CourseGraph cg = new CourseGraph(courseSet);
        assertEquals(aPreq, cg.getPreRequisites("a"));
        assertEquals(bPreq, cg.getPreRequisites("b"));
        assertEquals(cPreq, cg.getPreRequisites("c"));

        Set<String> apost = new HashSet<>();
        Set<String> bpost = new HashSet<>();
        bpost.add("c");
        apost.add("c");
        Set<String> cpost = new HashSet<>();

        assertEquals(apost, cg.getPostRequisites("a"));
        assertEquals(bpost, cg.getPostRequisites("b"));
        assertEquals(cpost, cg.getPostRequisites("c"));
    }

    @Test
    public void getCourseTest() {
        CourseGraph cg = new CourseGraph(courseSet);
        assertEquals(a, cg.getCourse("a"));
        assertEquals(b, cg.getCourse("b"));
        assertEquals(c, cg.getCourse("c"));
    }

    @Test void getAllPreqs() {
        CourseGraph cg = new CourseGraph(courseSet);
        assertEquals(aPreq, cg.getAllPreRequisites("a"));
        assertEquals(bPreq, cg.getAllPreRequisites("b"));
        assertEquals(cPreq, cg.getAllPreRequisites("c"));
    }

    @Test void getAllPostr() {
        CourseGraph cg = new CourseGraph(courseSet);
        assertEquals(new HashSet<>(), cg.getAllPostRequisites("c"));
    }
    @Test void illegalRequests() {
        CourseGraph cg = new CourseGraph(courseSet);
        assertThrows(IllegalArgumentException.class, () -> cg.getCourse("FAKE_COURSE"));
        assertThrows(IllegalArgumentException.class, () -> cg.getCoRequisites("FAKE_COURSE"));

        // in the spec for the two methods if the course is invalid it should
        // return an empty set
        assertEquals(new HashSet<>(), cg.getPreRequisites("FAKE_COURSE"));
        assertEquals(new HashSet<>(), cg.getPostRequisites("FAKE_COURSE"));
    }

}
