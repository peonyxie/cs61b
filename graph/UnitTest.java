package graph;

import org.junit.Test;
import ucb.junit.textui;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/* You MAY add public @Test methods to this class.  You may also add
 * additional public classes containing "Testing" in their name. These
 * may not be part of your graph package per se (that is, it must be
 * possible to remove them and still have your package work). */

/** Unit tests for the graph package.  This class serves to dispatch
 *  other test classes, which are listed in the argument to runClasses.
 *  @author Yuhan Xie
 */
public class UnitTest {


    /** Run all JUnit tests in the graph package. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(graph.GraphTest.class));
    }

    private class DFSPostOrder extends DepthFirstTraversal {

        ArrayList<Integer> order;

        DFSPostOrder(Graph G) {
            super(G);
            order = new ArrayList<>();
        }

        @Override
        protected boolean postVisit(int v) {
            order.add(v);
            return true;
        }

    }

    @Test
    public void dfsTest() {
        LabeledGraph<Integer, Integer> g =
                new LabeledGraph<>(new DirectedGraph());
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(1, 3);
        g.add(2, 3);
        g.add(3, 4);
        DFSPostOrder traversal = new DFSPostOrder(g);
        traversal.traverse(1);

        ArrayList<Integer> actual = traversal.order;
        ArrayList<Integer> expected =
                new ArrayList<>(Arrays.asList(4, 3, 2, 1));

        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
        assertEquals(expected.size(), actual.size());
    }


    private class ShortestPathDummy extends SimpleShortestPaths {
        ShortestPathDummy(Graph G, int source) {
            super(G, source);
        }

        protected double getWeight(int u, int v) {
            return 1.0;
        }
    }

    @Test
    public void shortestPathTest() {
        LabeledGraph<Integer, Integer> g =
                new LabeledGraph<>(new DirectedGraph());
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 3);
        g.add(2, 3);
        g.add(3, 4);
        g.add(1, 2);

        ShortestPathDummy dij = new ShortestPathDummy(g, 1);
        dij.setPaths();

        double[] expected = new double[]{0, 0, 1, 1, 2};
        double[] actual = dij.distance;

        for (int i = 1; i < actual.length; i++) {
            assertEquals(expected[i], actual[i], 0.01);
        }
        assertEquals(expected.length, actual.length);


        ArrayList<Integer> expectedPath =
                new ArrayList<>(Arrays.asList(1, 3, 4));
        LinkedList<Integer> actualPath = (LinkedList<Integer>) dij.pathTo(4);
        for (int i = 0; i < actualPath.size(); i++) {
            assertEquals(expectedPath.get(i), actualPath.get(i));
        }
        assertEquals(expectedPath.size(), actualPath.size());
    }

}
