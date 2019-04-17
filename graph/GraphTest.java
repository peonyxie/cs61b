package graph;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashSet;

import java.util.Set;
import java.util.List;

import java.util.Arrays;

import static org.junit.Assert.*;

/** Unit tests for the Graph class.
 *  @author Yuhan Xie
 */
public class GraphTest {

    @Test
    public void emptyGraph() {
        DirectedGraph g = new DirectedGraph();
        assertEquals("Initial graph has vertices", 0, g.vertexSize());
        assertEquals("Initial graph has edges", 0, g.edgeSize());
    }

    @Test
    public void contains() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(1, 3);
        g.add(2, 3);
        g.add(3, 4);

        assertEquals("Missing vertix", true, g.contains(1));
        assertEquals("Missing vertix", false, g.contains(0));
        assertEquals("Missing vertix", true, g.contains(3));
        assertEquals("Missing edge", true, g.contains(1, 2));
        assertEquals("Missing edge", false, g.contains(1, 4));
        assertEquals("Missing edge", 5,  g.add());
        assertEquals("Wrong Vertix Size", 5, g.vertexSize());
        assertEquals("Wrong Edge Size", 4, g.edgeSize());


    }
    @Test
    public void remove() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(1, 3);
        g.add(2, 3);
        g.add(3, 4);
        g.remove(3);

        assertEquals("Missing vertix", false, g.contains(3));
        assertEquals("Missing egde", false, g.contains(1, 3));
        assertEquals("Wrong Vertix Size", 3, g.vertexSize());
        assertEquals("Wrong Edge Size", 1, g.edgeSize());
    }

    @Test
    public void directiterator() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(1, 3);
        g.add(2, 3);
        g.add(3, 4);
        g.add(1, 4);

        Iterator<Integer> iterator = g.successors(1);

        int countS = 0;
        while (iterator.hasNext()) {
            int i = iterator.next();
            assertTrue(FROM_1.contains(i));
            countS += 1;
        }
        assertEquals(FROM_1.size(), countS);

        int countP = 0;
        Iterator<Integer> iterator2 = g.predecessors(4);
        while (iterator2.hasNext()) {
            int i = iterator2.next();
            assertTrue(TO_4.contains(i));
            countP += 1;
        }
        assertEquals(TO_4.size(), countP);

        int countV = 0;
        Iterator<Integer> iterator3 = g.vertices();
        while (iterator3.hasNext()) {
            int i = iterator3.next();
            assertTrue(VER.contains(i));
            countV += 1;
        }
        assertEquals(VER.size(), countV);

        int countE = 0;
        Iterator<int[]> iterator4 = g.edges();
        while (iterator4.hasNext()) {
            int[] i = iterator4.next();
            countE += 1;
        }
        assertEquals(EDGE.size(), countE);

    }
    static final ArrayList<Integer> FROM_1 = new ArrayList<Integer>(
            Arrays.asList(2, 3, 4));
    static final Set<Integer> TO_4 =
            new HashSet<>(Arrays.asList(1, 3));
    static final Set<Integer> VER =
            new HashSet<>(Arrays.asList(1, 2, 3, 4));
    static final List<Integer[]> EDGE = Arrays.asList
            (new Integer[][]{{1, 2}, {1, 3}, {1, 4}, {2, 3}, {3, 4}});

    @Test
    public void undirectcontains() {
        UndirectedGraph g = new UndirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(1, 3);
        g.add(2, 3);
        g.add(3, 4);
        g.remove(3, 4);

        assertEquals("Missing edge", true, g.contains(1, 2));
        assertEquals("Missing edge", true, g.contains(2, 1));
        assertEquals("Extra edge", false, g.contains(4, 3));


    }

    @Test
    public void undirectiterator() {
        UndirectedGraph g = new UndirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(1, 3);
        g.add(2, 3);
        g.add(3, 4);
        g.add(1, 4);

        Iterator<Integer> iterator = g.successors(3);

        int countS = 0;
        while (iterator.hasNext()) {
            int i = iterator.next();
            assertTrue(TO_3.contains(i));
            countS += 1;
        }
        assertEquals(TO_3.size(), countS);

        int countP = 0;
        Iterator<Integer> iterator2 = g.predecessors(3);
        while (iterator2.hasNext()) {
            int i = iterator2.next();
            assertTrue(TO_3.contains(i));
            countP += 1;
        }
        assertEquals(TO_3.size(), countP);

    }
    static final ArrayList<Integer> TO_3 = new ArrayList<Integer>(
            Arrays.asList(1, 2, 4));
}
