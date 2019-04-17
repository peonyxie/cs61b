package graph;

/* See restrictions in Graph.java. */


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;


/** A partial implementation of Graph containing elements common to
 *  directed and undirected graphs.
 *
 *  @author Yuhan Xie
 */
abstract class GraphObj extends Graph {

    /** Vertice size. */
    private int _V;

    /** Edge Size. */
    private int _E;

    /** Intarraylist. */
    private ArrayList<LinkedList<Integer>> inListArray;

    /** Outarraylist. */
    private ArrayList<LinkedList<Integer>> outListArray;

    /** Selfedge. */
    private ArrayList<Integer> selfEdges;

    /** EdgeList. */
    private ArrayList<Pair> edgeList;


    /**
     * A new, empty Graph.
     */
    GraphObj() {
        this._V = 0;
        this._E = 0;
        inListArray = new ArrayList<>();
        inListArray.add(null);
        outListArray = new ArrayList<>();
        outListArray.add(null);
        selfEdges = new ArrayList<>();
        selfEdges.add(-1);
        edgeList = new ArrayList<>();
        edgeList.add(null);
    }

    /** get selfedge.
     * @return selfedge */
    public ArrayList<Integer> getSelfEdges() {
        return this.selfEdges;
    }

    /** get out.
     * @return out */
    public ArrayList<LinkedList<Integer>> getoutList() {
        return this.outListArray;
    }

    /** get in.
     * @return in */
    public ArrayList<LinkedList<Integer>> getinList() {
        return this.inListArray;
    }

    /** Private Class Pair. */
    private class Pair {
        /** From. */
        private final int from;

        /** To. */
        private final int to;

        /** Pair.
         * @param a from.
         * @param b to.*/
        Pair(int a, int b) {
            this.from = a;
            this.to = b;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Pair)) {
                return false;
            }
            Pair otherPair = (Pair) other;
            return this.from == otherPair.from && this.to == otherPair.to;
        }

        @Override
        public int hashCode() {
            return this.from + this.to;
        }
    }

    @Override
    public int vertexSize() {
        return this._V;
    }

    @Override
    public int maxVertex() {
        int max = 0;
        for (int i = 1; i < inListArray.size(); i++) {
            if (inListArray.get(i) != null) {
                max = i;
            }
        }
        return max;
    }

    @Override
    public int edgeSize() {
        return this._E;
    }

    @Override
    public abstract boolean isDirected();

    @Override
    public int outDegree(int v) {
        if (inListArray.get(v) == null) {
            return 0;
        }
        return inListArray.get(v).size() + selfEdges.get(v);
    }
    @Override
    public abstract int inDegree(int v);

    @Override
    public boolean contains(int u) {
        return u <= maxVertex() && (inListArray.get(u) != null);
    }

    @Override
    public boolean contains(int u, int v) {
        if (!(this.contains(u) && this.contains(v))) {
            return false;
        }
        if (u == v) {
            return selfEdges.get(u) == 1;
        }
        for (int i = 1; i < edgeList.size(); i++) {
            if (edgeList.get(i) == null) {
                continue;
            }
            if (edgeList.get(i).from == u && edgeList.get(i).to == v) {
                return true;
            }
        }
        return false;
    }
    @Override
    public int add() {
        int firstNull = 0;
        for (int i = 1; i < inListArray.size(); i++) {
            if (inListArray.get(i) == null) {
                firstNull = i;
                break;
            }
        }
        if (firstNull == 0) {
            inListArray.add(new LinkedList<>());
            outListArray.add(new LinkedList<>());
            selfEdges.add(0);
            firstNull = inListArray.size() - 1;
        } else {
            inListArray.set(firstNull, new LinkedList<>());
            outListArray.set(firstNull, new LinkedList<>());
        }
        this._V += 1;
        return firstNull;
    }


    @Override
    public int add(int u, int v) {
        if (!(this.contains(u) && this.contains(v) || this.contains(u, v))) {
            throw new Error("No such element/or element already exists.");
        }
        if (u == v) {
            selfEdges.set(u, 1);
        } else {
            inListArray.get(u).add(v);
            outListArray.get(v).add(u);
        }
        Pair edge = new Pair(u, v);
        this._E += 1;
        edgeList.add(edge);
        return edgeId(u, v);
    }

    @Override
    public void remove(int v) {
        if (!contains(v)) {
            return;
        }
        LinkedList<Integer> inList = inListArray.get(v),
                outList = outListArray.get(v);
        while (inList.size() > 0) {
            remove(v, inList.get(0));
        }

        while (outList.size() > 0) {
            remove(outList.get(0), v);
        }
        remove(v, v);
        inListArray.set(v, null);
        outListArray.set(v, null);
        this._V -= 1;
    }

    @Override
    public void remove(int u, int v) {
        if (!this.contains(u, v)) {
            return;
        }
        Pair remove = new Pair(u, v);
        edgeList.set(edgeList.indexOf(remove), null);
        this._E -= 1;
        if (u == v) {
            selfEdges.set(u, 0);
        } else {
            LinkedList<Integer> inList = inListArray.get(u),
                    outList = outListArray.get(v);
            inList.remove(inList.indexOf(v));
            outList.remove(outList.indexOf(u));
        }
    }

    @Override
    public Iteration<Integer> vertices() {
        Iteration<Integer> iterator = new Iteration<Integer>() {
            private int position = 0;

            @Override
            public boolean hasNext() {
                for (int i = position + 1; i <= maxVertex(); i++) {
                    if (inListArray.get(i) != null) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Integer next() {
                while (hasNext()) {
                    if (inListArray.get(position + 1) != null) {
                        position++;
                        return position;
                    } else {
                        position++;
                        return next();
                    }
                }
                throw new Error("No more vertix.");
            }
        };
        return iterator;
    }


    @Override
    public Iteration<Integer> successors(int v) {
        if (!contains(v)) {
            Iterator<Integer> empty = Collections.emptyIterator();
            return Iteration.iteration(empty);
        }
        Iteration<Integer> iterator = new Iteration<>() {
            private boolean selfEdge = selfEdges.get(v) == 1;
            private int position = 0;
            @Override
            public boolean hasNext() {
                return inListArray.get(v).size() > position || selfEdge;
            }

            public Integer next() {
                if (selfEdge) {
                    selfEdge = false;
                    return v;
                }
                int result = inListArray.get(v).get(position);
                position++;
                return result;
            }
        };
        return iterator;
    }

    @Override
    public abstract Iteration<Integer> predecessors(int v);

    @Override
    public Iteration<int[]> edges() {
        Iteration<int[]> iterator = new Iteration<int[]>() {
            private int position = -1;

            @Override
            public boolean hasNext() {
                for (int i = position + 1; i < edgeList.size(); i++) {
                    if (edgeList.get(i) != null) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public int[] next() {
                if (edgeList.get(position + 1) != null) {
                    int[] result = new int[2];
                    result[0] = edgeList.get(position + 1).from;
                    result[1] = edgeList.get(position + 1).to;
                    position++;
                    return result;
                } else {
                    position++;
                    return next();
                }
            }
        };
        return iterator;
    }

    @Override
    protected void checkMyVertex(int v) {
    }

    @Override
    protected int edgeId(int u, int v) {
        int id = edgeList.indexOf(new Pair(u, v));
        if (id > 0) {
            return id;
        }
        throw new Error("No such edge.");
    }
}
