package axr170131;

/* Driver code for PERT algorithm (LP4)
 * @author
 * Team members: (LP1)
Akshaya Ramaswamy (axr170131)
Sheetal Kadam (sak170006)
Meghna Mathur (mxm180022)
Maleeha Koul  (msk180001)

 */

import rbk.Graph.Vertex;
import rbk.Graph;
import rbk.Graph.Edge;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Factory;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class PERT extends GraphAlgorithm<PERT.PERTVertex> {
    public static class PERTVertex implements Factory {
        int duration;
        int ec, lc;
        int slack;

        public PERTVertex(Vertex u) {
            duration = 0;
            ec = 0;
            lc = 0;
            slack = 0;
        }

        public PERTVertex make(Vertex u) {
            return new PERTVertex(u);
        }
    }

    public PERT(Graph g) {
        super(g, new PERTVertex(null));
    }

    // getter and setter methods to retrieve and update vertex properties

    // Earliest completion time of u

    public int ec(Vertex u) {
        return get(u).ec;
    }

    public void setEC(Vertex u, int value) {
        get(u).ec = value;
    }

    // Latest completion time of u

    public int lc(Vertex u) {
        return get(u).lc;
    }

    public void setLC(Vertex u, int value) {
        get(u).lc = value;
    }

    // Slack of u
    public int slack(Vertex u) {
        return get(u).slack;
    }

    // Duration of u

    public int getDuration(Vertex u) {
        return get(u).duration;
    }

    public void setDuration(Vertex u, int d) {
        get(u).duration = d;
    }

    public void setSlack(Vertex u, int slack) {
        get(u).slack = slack;
    }

    // --------------------Utility methods for ec and lc----------------------------

    public void initializeEC(int value) {
        for (Vertex u : g) {
            setEC(u, value);
        }
    }

    public void initializeLC(int value) {
        for (Vertex u : g) {
            setLC(u, value);
        }
    }

    private void calculateLC(Vertex u) {
        // latest time at which u can be started without delaying the project
        // is min lc of successors of u - duration of u

        for (Edge e : g.incident(u)) {
            Vertex v = e.otherEnd(u);

            if (lc(v) - getDuration(v) < lc(u)) { // get minimum
                int updatedLC = lc(v) - getDuration(v);
                setLC(u, updatedLC);
            }
        }
    }

    private void calculateEC(Vertex u) {
        // earliest time at which u can be completed is max ec
        // of predecessors + duration of u

        for (Edge e : g.incident(u)) {
            Vertex v = e.otherEnd(u);

            if (ec(u) + getDuration(v) > ec(v)) { // get maximum
                int updatedEC = ec(u) + getDuration(v);
                setEC(v, updatedEC);
            }
        }
    }

    /**
     * This method is used to ad dummy nodes s and t to the graph . s is start node
     * that is predecessor to all tasks t is completion node that is successor to
     * all tasks
     */

    public void addDummyNodes() {

        Vertex s = g.getVertex(1);
        Vertex t = g.getVertex(g.size());
        int m = g.edgeSize();

        for (int i = 2; i < g.size(); i++) {
            g.addEdge(s, g.getVertex(i), 1, ++m); // add edge from s to all vertices
            g.addEdge(g.getVertex(i), t, 1, ++m); // add edge from to all vertices to t
        }
    }

    // -------------------- PERT----------------------------

    // non-static method called after calling the constructor
    public boolean pert() {

        addDummyNodes(); // add start and completion vertex (s and t)
        List<Vertex> list = DFS.topologicalOrder1(g);

        if (list == null) {
            return true;
        } // not a DAG

        else {
            // Computation of ec

            initializeEC(0);// set ec of all tasks to 0
            for (Vertex u : list) { // LI: u.ec has been calculated
                calculateEC(u);
            }

            //computation of lc

            Vertex t = list.get(list.size() - 1); // get last vertex t
            int maxTime = ec(t);
            initializeLC(maxTime);

            // iterate over vertices in descending order
            Iterator<Vertex> it = ((LinkedList) list).descendingIterator();

            while (it.hasNext()) {
                Vertex u = it.next();
                calculateLC(u);

                // calculate slack
                int slack = lc(u) - ec(u);
                setSlack(u, slack);
            }
            return false;
        }
    }

    // Length of critical path
    public int criticalPath() {
        Vertex t = g.getVertex(g.size());
        return lc(t);

    }

    // Is vertex u on a critical path?
    public boolean critical(Vertex u) {

        if (slack(u) == 0)
            return true;
        return false;
    }

    // Number of critical nodes in graph
    public int numCritical() {

        int numCriticalNodes = 0;
        for (Vertex u : g) {
            if (critical(u)) {
                numCriticalNodes++;
            }
        }
        return numCriticalNodes;
    }

    // static method to run PERT algorithm on graph g

    public static PERT pert(Graph g, int[] duration) {
        PERT p = new PERT(g);

        for (Vertex u : g) {
            p.setDuration(u, duration[u.getIndex()]);
        }
        p.pert();
        return p;
    }

    public static void main(String[] args) throws Exception {
        String graph = "11 12   2 4 1   2 5 1   3 5 1   3 6 1   4 7 1   5 7 1   5 8 1   6 8 1   6 9 1   7 10 1   8 10 1   9 10 1      0 3 2 3 2 1 3 2 4 1 0";
//        String graph="4 3  1 3 1  2 4 1   3 4 1  0 3 2 3";

        Scanner in;
        // If there is a command line argument, use it as file from which
        // input is read, otherwise use input from string.
        in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(graph);
        Graph g = Graph.readDirectedGraph(in);
        g.printGraph(false);

        PERT p = new PERT(g);
        for (Vertex u : g) {
            p.setDuration(u, in.nextInt());
        }
        // Run PERT algorithm. Returns null if g is not a DAG
        if (p.pert()) {
            System.out.println("Invalid graph: not a DAG");
        } else {
            System.out.println("Number of critical vertices: " + p.numCritical());
            System.out.println("u\tEC\tLC\tSlack\tCritical");
            for (Vertex u : g) {
                System.out.println(u + "\t" + p.ec(u) + "\t" + p.lc(u) + "\t" + p.slack(u) + "\t" + p.critical(u));
            }
        }
    }
}