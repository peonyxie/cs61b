package graph;

/* See restrictions in Graph.java. */


import java.util.Iterator;

import java.util.Collections;

import java.util.LinkedList;

/** Represents an undirected graph.  Out edges and in edges are not
 *  distinguished.  Likewise for successors and predecessors.
 *
 *  @author yuhan xie
 */
public class UndirectedGraph extends GraphObj {

    @Override
    public boolean isDirected() {
        return false;
    }

    @Override
    public boolean contains(int u, int v) {
        return super.contains(u, v) || super.contains(v, u);
    }

    @Override
    public void remove(int u, int v) {
        super.remove(u, v);
        super.remove(v, u);
    }

    @Override
    public int outDegree(int v) {
        LinkedList<Integer> inList = getinList().get(v),
                outList = getoutList().get(v);
        if (inList == null) {
            return 0;
        }
        return inList.size() + outList.size() + getSelfEdges().get(v);
    }
    @Override
    public int inDegree(int v) {
        return outDegree(v);
    }

    @Override
    public Iteration<Integer> predecessors(int v) {
        if (!contains(v)) {
            Iterator<Integer> empty = Collections.emptyIterator();
            return Iteration.iteration(empty);
        }
        Iterator<Integer> second = super.successors(v);
        Iteration<Integer> iterator = new Iteration<>() {
            private int position = 0;
            @Override
            public boolean hasNext() {
                return getoutList().get(v).size()
                        > position || second.hasNext();
            }
            public Integer next() {
                if (getoutList().get(v).size() > position) {
                    int result = getoutList().get(v).get(position);
                    position++;
                    return result;
                } else {
                    return second.next();
                }
            }
        };
        return iterator;
    }
    @Override
    public Iteration<Integer> successors(int v) {
        return predecessors(v);
    }

}
