package graph;

/* See restrictions in Graph.java. */

import java.util.PriorityQueue;

import java.util.List;

import java.util.LinkedList;



/** The shortest paths through an edge-weighted graph.
 *  By overrriding methods getWeight, setWeight, getPredecessor, and
 *  setPredecessor, the client can determine how to represent the weighting
 *  and the search results.  By overriding estimatedDistance, clients
 *  can search for paths to specific destinations using A* search.
 *  @author Yuhan Xie
 */
public abstract class ShortestPaths {

    /** distance. */
    protected double[] distance;


    /** previous. */
    protected int[] prev;


    /** found. */
    private boolean[] found;

    /** The shortest paths in G from SOURCE. */
    public ShortestPaths(Graph G, int source) {
        this(G, source, 0);
    }

    /** A shortest path in G from SOURCE to DEST. */
    public ShortestPaths(Graph G, int source, int dest) {
        _G = G;
        _source = source;
        _dest = dest;

        int size = _G.maxVertex() + 1;
        distance = new double[size];
        prev = new int[size];
        found = new boolean[size];
    }



    /** Pair class. */
    private class Pair implements Comparable<Pair> {


        /** The distance. */
        private double dis;


        /** The index. */
        private int index;


        /** Class Pair.
         * @param a dis.
         * @param b index */
        Pair(double a, int b) {
            this.dis = a;
            this.index = b;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null || !(other instanceof Pair)) {
                return false;
            }
            Pair otherPair = (Pair) other;
            return this.dis == otherPair.dis && this.index == otherPair.index;
        }

        @Override
        public int hashCode() {
            Double dist = this.dis;
            int dis2 = dist.intValue();
            return dis2 + this.index;
        }

        @Override
        public int compareTo(Pair other) {
            if (this.dis < other.dis) {
                return -1;
            } else if (this.dis > other.dis) {
                return 1;
            }
            return 0;
        }
    }

    /** Initialize the shortest paths.  Must be called before using
     *  getWeight, getPredecessor, and pathTo. */
    public void setPaths() {
        PriorityQueue<Pair> fringe = new PriorityQueue<>();

        for (int i : _G.vertices()) {
            distance[i] = Integer.MAX_VALUE;
            prev[i] = 0;
            found[i] = false;
        }
        distance[_source] = 0;
        for (int index : _G.vertices()) {
            fringe.add(new Pair(distance[index], index));
        }

        while (!fringe.isEmpty()) {
            Pair shortest = fringe.remove();
            int current = shortest.index;

            if (found[current]) {
                continue;
            }
            found[current] = true;

            if (current != _dest) {
                for (int neighbor : _G.successors(current)) {
                    double newDis = getWeight(current)
                            + getWeight(current, neighbor);
                    if (newDis < distance[neighbor]) {
                        fringe.add(new Pair(newDis
                                + estimatedDistance(neighbor), neighbor));
                        distance[neighbor] = newDis;
                        prev[neighbor] = current;
                    }
                }
            } else {
                return;
            }
        }
    }


    /** Returns the starting vertex. */
    public int getSource() {
        return _source;
    }

    /** Returns the target vertex, or 0 if there is none. */
    public int getDest() {
        return _dest;
    }

    /** Returns the current weight of vertex V in the graph.  If V is
     *  not in the graph, returns positive infinity. */
    public abstract double getWeight(int v);

    /** Set getWeight(V) to W. Assumes V is in the graph. */
    protected abstract void setWeight(int v, double w);

    /** Returns the current predecessor vertex of vertex V in the graph, or 0 if
     *  V is not in the graph or has no predecessor. */
    public abstract int getPredecessor(int v);

    /** Set getPredecessor(V) to U. */
    protected abstract void setPredecessor(int v, int u);

    /** Returns an estimated heuristic weight of the shortest path from vertex
     *  V to the destination vertex (if any).  This is assumed to be less
     *  than the actual weight, and is 0 by default. */
    protected double estimatedDistance(int v) {
        return 0.0;
    }

    /** Returns the current weight of edge (U, V) in the graph.  If (U, V) is
     *  not in the graph, returns positive infinity. */
    protected abstract double getWeight(int u, int v);

    /** Returns a list of vertices starting at _source and ending
     *  at V that represents a shortest path to V.  Invalid if there is a
     *  destination vertex other than V. */
    public List<Integer> pathTo(int v) {
        LinkedList<Integer> path = new LinkedList<>();
        while (v != _source) {
            path.addFirst(v);
            v = prev[v];
        }
        path.addFirst(_source);
        return path;
    }

    /** Returns a list of vertices starting at the source and ending at the
     *  destination vertex. Invalid if the destination is not specified. */
    public List<Integer> pathTo() {
        return pathTo(getDest());
    }


    /** The graph being searched. */
    protected final Graph _G;
    /** The starting vertex. */
    private final int _source;
    /** The target vertex. */
    private final int _dest;


}

