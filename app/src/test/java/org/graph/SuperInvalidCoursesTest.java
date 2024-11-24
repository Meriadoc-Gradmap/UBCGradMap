package org.graph;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SuperInvalidCoursesTest {

    double[] acred;
    double[] bcred;
    double[] ccred;

    Set<String> aPreq;
    Set<String> aCo;
    Set<String> bPreq;
    Set<String> bCo;
    Set<String> cPreq;

    Hours aHours;
    Hours bHours;
    Hours cHours;

    Others aOther;
    Others bOther;
    Others cOther;

    Course a;

    Course b;

    Course c;

    CourseGraph cg;

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

        a = mock(Course.class, withSettings().useConstructor(
                "a",
                "course a",
                acred,
                "desc a",
                aPreq,
                aCo,
                false,
                aHours,
                aOther,
                0).defaultAnswer(CALLS_REAL_METHODS));
        b = mock(Course.class, withSettings().useConstructor(
                "b",
                "course b",
                bcred,
                "desc b",
                bPreq,
                bCo,
                false,
                bHours,
                bOther,
                1).defaultAnswer(CALLS_REAL_METHODS));
        c = mock(Course.class, withSettings().useConstructor(
                "c",
                "course c",
                ccred,
                "desc c",
                cPreq,
                new HashSet<>(),
                false,
                cHours,
                cOther,
                2).defaultAnswer(CALLS_REAL_METHODS));

        courseSet = new HashSet<>();
        courseSet.add(a);
        courseSet.add(b);
        courseSet.add(c);

        when(a.id()).thenReturn(-12);
        when(b.id()).thenReturn(208);
        when(c.id()).thenReturn(0);

    }

    @Test
    public void invalidCourseGroupIndices(){
        assertEquals(-12, a.id());
        Set<Course> onlyA = new HashSet<>();
        onlyA.add(a);
        assertThrows(IllegalArgumentException.class, () -> new CourseGraph(onlyA));
        assertEquals(208, b.id());
        assertEquals(0, c.id());
        assertThrows(IllegalArgumentException.class, () -> cg = new CourseGraph(courseSet));
    }
}
