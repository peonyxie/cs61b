package graph;

/* See restrictions in Graph.java. */

/** A partial implementation of ShortestPaths that contains the weights of
 *  the vertices and the predecessor edges.   The client needs to
 *  supply only the two-argument getWeight method.
 *  @author Yuhan Xie
 */
public abstract class SimpleShortestPaths extends ShortestPaths {


    /** Label G. */
    private double[] label;

    /** predecessors. */
    private int[] predecessor;
    /** The shortest paths in G from SOURCE. */
    public SimpleShortestPaths(Graph G, int source) {
        this(G, source, 0);
    }

    /** A shortest path in G from SOURCE to DEST. */
    public SimpleShortestPaths(Graph G, int source, int dest) {
        super(G, source, dest);
    }

    /** Returns the current weight of edge (U, V) in the graph.  If (U, V) is
     *  not in the graph, returns positive infinity. */
    @Override
    protected abstract double getWeight(int u, int v);

    @Override
    public double getWeight(int v) {
        return distance[v];
    }

    @Override
    protected void setWeight(int v, double w) {
        label[v] = w;
    }

    @Override
    public int getPredecessor(int v) {
        return prev[v];
    }

    @Override
    protected void setPredecessor(int v, int u) {
        predecessor[v] = u;
    }
}
