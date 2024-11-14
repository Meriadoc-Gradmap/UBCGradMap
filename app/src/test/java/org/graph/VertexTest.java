package org.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VertexTest {

    @Test
    public void invalidVertexTest(){
        // invalid constructor
        Vertex v1 = new Vertex(1);
//        assertTrue()
        assertThrows(IllegalArgumentException.class, () ->
                new Vertex(-1)
        );

        assertThrows(IllegalArgumentException.class, () -> v1.setId(-1));
    }

    @Test
    public void getSetID(){
        Vertex v1 = new Vertex(1);
        assertEquals(1, v1.id());
        v1.setId(2);
        assertEquals(2, v1.id());
    }

    @Test
    public void equalsTest(){
        Vertex v1 = new Vertex(1);
        Vertex v2 = new Vertex(2);

        assertEquals(v1,v1);
        assertEquals(v2, v2);
        assertNotEquals(v1, v2);

        assertEquals(v1.hashCode(), v1.hashCode());
        assertEquals(v2.hashCode(), v2.hashCode());
        assertNotEquals(v1.hashCode(), v2.hashCode());
    }

}
