
import Graph.Vertex;

import Graph;
import Graph.Edge;
import Graph.GraphAlgorithm;
import Graph.Factory;
import Graph.Timer;

import java.io.File;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class DFS extends GraphAlgorithm<DFS.DFSVertex> {
    LinkedList<Vertex> finishList = new LinkedList<Vertex>();;
    int topNum; // keep track of order added in finsihlist
    int cno; // number of components in graph
    boolean cycle; // graph has cycle or not

    public static class DFSVertex implements Factory {
        int cno;
        private boolean seen;
        Vertex parent;
        int top; // store the order of add in finishlist

        public DFSVertex(Vertex u) {
            seen = false;
            parent = null;
            cno = 0;
            top = 0;
        }

        public DFSVertex make(Vertex u) {
            return new DFSVertex(u);
        }
    }

    public DFS(Graph g) {
        super(g, new DFSVertex(null));
        topNum = 0;
        cno = 0;
        cycle = false;

    }

    // getter and setter methods to retrieve and update vertex properties
    public boolean getSeen(Vertex u) {
        return get(u).seen;
    }

    public void setSeen(Vertex u, boolean value) {
        get(u).seen = value;
    }

    public Vertex getParent(Vertex u) {
        return get(u).parent;
    }

    public void setParent(Vertex u, Vertex p) {
        get(u).parent = p;
    }

    public static DFS stronglyConnectedComponents(Graph g) {
        DFS d = new DFS(g);
        d.dfs();
        LinkedList<Vertex> list = d.finishList; // store the finish list as it will be lost
        g.reverseGraph();
        d.dfs(list); // rub dfs over finish list of original graph
        g.reverseGraph();
        return d;

    }

    private void dfs() {
        dfs(g);
    }

// run dfs on graph g

    private void dfs(Iterable<Vertex> iter) {

        initialize(); // reset instance variables of graph
        topNum = g.size();
        for (Vertex u : iter) {
            if (!get(u).seen) {
                cno++;
                dfsVisit(u);

            }
        }
    }

    public void initialize() {
        for (Vertex u : g) {
            setSeen(u, false);
            setParent(u, null);
        }
        finishList = new LinkedList<Vertex>();
        cno = 0;
    }

    // recursive function to traverse all vertices in same component
    private void dfsVisit(Vertex u) {
        setSeen(u, true);
        get(u).cno = cno;

        for (Edge e : g.incident(u)) {
            // if the other end vertex is still being processed but has been visited then
            // cycle
            if (getSeen(e.otherEnd(u)) && get(e.otherEnd(u)).top == 0) {
                cycle = true;
            }

            if (!getSeen(e.otherEnd(u))) {
                setParent(e.otherEnd(u), u);
                dfsVisit(e.otherEnd(u));
            }

        }
        // add to finishlist
        get(u).top = topNum--;
        finishList.addFirst(u);

    }

    // call dfs
    public static DFS depthFirstSearch(Graph g) {
        DFS d = new DFS(g);
        d.dfs();
        return d;
    }

    // Member function to find topological order
    public List<Vertex> topologicalOrder1() {
        return finishList; // topological order is stored in finish list
    }

    // Find the number of connected components of the graph g by running dfs.
    // Enter the component number of each vertex u in u.cno.
    // Note that the graph g is available as a class field via GraphAlgorithm.

    public int connectedComponents() {
        return cno;
    }

    public static int connectedComponents(Graph g) {
        DFS d = new DFS(g);

        return d.connectedComponents();
    }

    // After running the connected components algorithm, the component no of each
    // vertex can be queried.
    public int cno(Vertex u) {
        return get(u).cno;
    }

    // Find topological oder of a DAG using DFS. Returns null if g is not a DAG.
    public static List<Vertex> topologicalOrder1(Graph g) {
        DFS d = new DFS(g);
        d.dfs();
        // if graph has cycle or is undirected return null
        if (d.isDAG()) {
            return null;
        } else {
            return d.topologicalOrder1();
        }

    }

    public boolean isDAG() {
        return cycle || !g.isDirected();
    }

    // Find topological oder of a DAG using the second algorithm. Returns null if g
    // is not a DAG.
    public static List<Vertex> topologicalOrder2(Graph g) {
        return null;
    }

    public static void main(String[] args) throws Exception {
        String string = "9 13 1 2 2 2 3 3 2 4 5 3 3 4 3 6 1 4 2 2 4 7 7 5 1 1 5 4 4 6  8 8 7 5 5 8 9 9 9 6 6 0";
        //String string = "5 4  1 2 1  1 3 1    2 4 1  3 4 1  4 5 1  0";

        Scanner in;
        // If there is a command line argument, use it as file from which
        // input is read, otherwise use input from string.
        in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(string);

        // Read graph from input
        Graph g = Graph.readDirectedGraph(in);
        g.printGraph(false);

        DFS d = depthFirstSearch(g);

        System.out.println(topologicalOrder1(g));
        int numcc = d.connectedComponents();
        System.out.println("Number of components: " + numcc + "\nu\tcno");
        for (Vertex u : g) {
            System.out.println(u + "\t" + d.cno(u));
        }

        DFS strongly_connected = DFS.stronglyConnectedComponents(g);

        System.out.println("Strongly connected components are :" + strongly_connected.connectedComponents());
        for (Vertex u : g) {
            System.out.println(u + "\t" + strongly_connected.cno(u));
        }

    }

}
