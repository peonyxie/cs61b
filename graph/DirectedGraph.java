package graph;

/* See restrictions in Graph.java. */

import java.util.Collections;
import java.util.Iterator;

/** Represents a general unlabeled directed graph whose vertices are denoted by
 *  positive integers. Graphs may have self edges.
 *
 *  @author Yuhan Xie
 */
public class DirectedGraph extends GraphObj {

    @Override
    public boolean isDirected() {
        return true;
    }

    @Override
    public int inDegree(int v) {
        if (getoutList().get(v) == null) {
            return 0;
        }
        return getoutList().get(v).size() + getSelfEdges().get(v);
    }

    @Override
    public Iteration<Integer> predecessors(int v) {
        if (!contains(v)) {
            Iterator<Integer> empty = Collections.emptyIterator();
            return Iteration.iteration(empty);
        }
        Iteration<Integer> iterator = new Iteration<>() {
            private int position = 0;
            private boolean selfEdge = getSelfEdges().get(v) == 1;
            @Override
            public boolean hasNext() {
                return getoutList().get(v).size() > position || selfEdge;
            }
            public Integer next() {
                if (selfEdge) {
                    selfEdge = false;
                    return v;
                }
                int result = getoutList().get(v).get(position);
                position++;
                return result;
            }
        };
        return iterator;

    }


}
