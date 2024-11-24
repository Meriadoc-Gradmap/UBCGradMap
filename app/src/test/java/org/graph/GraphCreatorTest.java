package org.graph;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GraphCreatorTest {


    @Test
    public void createGraphFromTestJson(){
        // this test should just make a graph with CPEN-211 and CPEN-212
       CourseGraph cg = GraphCreator.createGraph("src/test/java/org/graph/testJson.json");
       assertTrue(cg.getCourse("CPEN-211") != null);
       assertTrue(cg.getCourse("CPEN-212") != null);
       Course cpen211 = cg.getCourse("CPEN-211");
       Course cpen212 = cg.getCourse("CPEN-212");

       // next we will make sure that all info from the courses has been brought over.
       // CPEN 211
        assertEquals("CPEN-211", cpen211.getCourseCode());
        assertEquals("Computing Systems I", cpen211.getName());
        assertEquals(5.0, cpen211.getCredits()[0]);
        assertEquals(1, cpen211.getCredits().length);
        assertEquals("Boolean algebra; combinational and sequential circuits; organization and operation of microcomputers, memory addressing modes, representation of information, instruction sets, machine and assembly language programming, systems programs, I/O structures, I/O interfacing and I/O programming, introduction to digital system design using microcomputers. Credit will be granted for only one of CPEN 211, CPEN 312, EECE 256, EECE 259 or EECE 355.  Prerequisite: APSC 160. ", cpen211.getDescription());
        assertEquals("APSC-160", cpen211.getPreRequisites().iterator().next());
        assertEquals(1, cpen211.getPreRequisites().size());
        assertEquals(0, cpen211.getCorequisites().size());
        assertFalse(cpen211.isCdf());
        Hours weekly211 = new Hours(4, false, 2, false, 2, true);
        assertEquals(weekly211, cpen211.getWeeklyHours());
        Others other211 = new Others(70.76);
        assertEquals(other211, cpen211.getOthers());
        assertEquals(70.76, cpen211.getAverage());

        // CPEN 212
        assertEquals("CPEN-212", cpen212.getCourseCode());
        assertEquals("Computing Systems II", cpen212.getName());
        assertEquals(4.0, cpen212.getCredits()[0]);
        assertEquals(1, cpen211.getCredits().length);
        assertEquals("Abstractions at the hardware-software interface and their low-level implementation. Procedure invocation, dynamic dispatch, and related exploits; library linkage, virtual memory, heap management, garbage collection, and caches; interrupts, signals, and processes; threads, locks, and cache coherence; files, devices, and network topology.  Prerequisite: CPEN 211 and one of CPEN 221, CPEN 223, CPSC 259. ", cpen212.getDescription());
        Set<String> prereq212 = new HashSet<>();
        prereq212.add("CPEN-221");
        prereq212.add("CPEN-211");
        prereq212.add("CPEN-223");
        prereq212.add("CPSC-259");
        assertEquals(prereq212, cpen212.getPreRequisites());
        assertEquals(4, cpen212.getPreRequisites().size());
        assertEquals(0, cpen212.getCorequisites().size());
        assertFalse(cpen211.isCdf());
        Hours weekly212 = new Hours(3, false, 2, false, 0, false);
        assertEquals(weekly212, cpen212.getWeeklyHours());
        Others other212 = new Others(67.47);
        assertEquals(other212, cpen212.getOthers());
        assertEquals(67.47, cpen212.getAverage());
    }

    @Test
    public void nofileCreator(){
        assertThrows(IllegalArgumentException.class, () ->
                GraphCreator.createGraph("DNE.json")
        );
    }
}
