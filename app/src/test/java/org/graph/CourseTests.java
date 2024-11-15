package org.graph;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CourseTests {


    @Test
    public void createValidCourse(){

        Set<String> prereqs = new HashSet<>();
        prereqs.add("pre1");
        prereqs.add("pre2");

        Set<String> coreqs = new HashSet<>();
        coreqs.add("co1");
        coreqs.add("co2");

        Course c = new Course(
                "code",
                "name",
                new double[]{0},
                "desc",
                prereqs,
                coreqs,
                false,
                new Hours(1, false, 1, false, 1, false),
                new Others(12, "dr. prof"),
                0
        );

        assertEquals("code", c.getCourseCode());
        assertEquals("name", c.getName());
        assertEquals(0, c.getCredits()[0]);
        assertEquals(1, c.getCredits().length);
        assertEquals("desc", c.getDescription());

        assertEquals(prereqs, c.getPreRequisites());
        assertNotSame(prereqs, c.getPreRequisites());
        // make sure it is a separate object
        assertEquals(coreqs, c.getCorequisites());
        assertNotSame(coreqs, c.getCorequisites());

        assertFalse(c.isCdf());
        assertEquals(new Hours(1, false, 1, false, 1, false), c.getWeeklyHours());
        assertEquals(new Others(12, "dr. prof"), c.getOthers());

        assertEquals(12, c.getAverage());
    }

    @Test
    public void equalsTest(){

        Set<String> prereqs = new HashSet<>();
        prereqs.add("pre1");
        prereqs.add("pre2");

        Set<String> postreqs = new HashSet<>();
        postreqs.add("post1");
        postreqs.add("post2");

        Course a = new Course(
                "code",
                "name",
                new double[]{0},
                "desc",
                prereqs,
                postreqs,
                false,
                new Hours(1, false, 1, false, 1, false),
                new Others(12, "dr. prof"),
                0
        );

        assertEquals(a, a);
    }

    @Test
    public void immutabilityTests(){

        Set<String> prereqs = new HashSet<>();
        prereqs.add("pre1");
        prereqs.add("pre2");

        Set<String> coreqs = new HashSet<>();
        coreqs.add("post1");
        coreqs.add("post2");

        double[] credits = new double[] {0};

        Hours h = new Hours(1, false, 1, false, 1, false);
        Others o = new Others(12, "dr. prof");

        Course a = new Course(
                "code",
                "name",
                credits,
                "desc",
                prereqs,
                coreqs,
                false,
                h,
                o,
                0
        );

        // credits
        double[] creditCopy = a.getCredits();
        creditCopy[0] = 100;
        assertNotEquals(creditCopy[0], a.getCredits()[0]);
        assertEquals(credits[0], a.getCredits()[0]);
        credits[0] = 100;
        assertNotEquals(credits[0], a.getCredits()[0]);

        // pre req
        Set<String> copiedPreqs = a.getPreRequisites();
        copiedPreqs.add("EVIL VALUD");
        assertNotEquals(copiedPreqs, a.getPreRequisites());
        assertEquals(prereqs, a.getPreRequisites());

        prereqs.add("EVIL");
        assertNotEquals(prereqs, a.getPreRequisites());

        // co req
        Set<String> copiedCoReq = a.getCorequisites();
        copiedCoReq.add("EVIL VALUD");
        assertNotEquals(copiedCoReq, a.getCorequisites());
        assertEquals(coreqs, a.getCorequisites());

        coreqs.add("EVIL");
        assertNotEquals(coreqs, a.getCorequisites());

        // weekly hours
        assertEquals(h, a.getWeeklyHours());
        Hours alsoH =  new Hours(1, false, 1, false, 1, false);
        assertEquals(alsoH, h);
        assertEquals(alsoH, a.getWeeklyHours());

        // other
        assertEquals(o, a.getOthers());
        Others alsoO = new Others(12, "dr. prof");
        assertEquals(alsoO, o);
        assertEquals(alsoO, a.getOthers());
    }


}
