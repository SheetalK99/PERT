package axr170131;

/* Starter code for enumerating topological orders of a DAG
 * @author
 * Team members: (LP1)
Akshaya Ramaswamy (axr170131)
Sheetal Kadam (sak170006)
Meghna Mathur (mxm180022)
Maleeha Koul  (msk180001)

 */

import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Timer;
import rbk.Graph.Vertex;
import rbk.Graph;
import rbk.Graph.Edge;
import rbk.Graph.Factory;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Array;
import java.util.Scanner;
import java.util.List;

public class EnumerateTopological extends GraphAlgorithm<EnumerateTopological.EnumVertex> {
    boolean print; // Set to true to print array in visit
    long count; // Number of permutations or combinations visited
    Selector sel;

    public EnumerateTopological(Graph g) {
        super(g, new EnumVertex());
        print = true;
        count = 0;
        sel = new Selector();
    }

    static class EnumVertex implements Factory {
        private boolean visited; // keep track of processed node
        int inDegree; // number of incoming edges to vertex

        EnumVertex() {
            this.visited = false;
            inDegree = -1;
        }

        public EnumVertex make(Vertex u) {
            return new EnumVertex();
        }

    }

    // getter and setter methods to retrieve and update vertex properties
    public boolean getVisited(Vertex u) {
        return get(u).visited;
    }

    public void setVisited(Vertex u, boolean value) {
        get(u).visited = value;
    }

    public int getInDegree(Vertex u) {
        return get(u).inDegree;
    }

    public void setInDegree(Vertex u, int value) {
        get(u).inDegree = value;
    }


    // -------------------- Utility methods----------------------------

    // utility methods of incrementing and decrementing in degree

    public void decrementInDegree(Vertex u) {
        get(u).inDegree--;
    }

    public void incrementInDegree(Vertex u) {
        get(u).inDegree++;
    }

    // set vertex properties
    public void initialise() {
        // get in degree from graph vertex and put it in EnumVertex
        for (Vertex u : g) {
            get(u).inDegree = u.inDegree();
        }
    }

    // -------------------- Approver----------------------------

    class Selector extends Enumerate.Approver<Vertex> {

        /**
         * This method is used to select nodes for enumeration
         * Node u with no incoming edges is selected and indegree of all the nodes
         * that have an edge out of u is decreased by 1(corresponds to removal of edge)
         */

        @Override
        public boolean select(Vertex u) {
            // select vertex if indegree of vertex is 0 and its not yet visited
            if (getInDegree(u) == 0 && !getVisited(u)) {
                setVisited(u, true);

                // After u is processed, remove it from graph that is reduce in degree
                // of all vertices v that are incident from u
                for (Edge e : g.incident(u)) {
                    Vertex v = e.otherEnd(u);
                    decrementInDegree(v);
                }
                return true;
            } else {
                return false;
            }

        }

        /**
         * This method is used to unselect node
         * Node u is made unvisited and indegree of all the nodes
         * that have an edge out of u in original graphis increased
         *by 1(corresponds to addition of edge)
         **/

        @Override
        public void unselect(Vertex u) {
            // while unselecting put u back to graph
            // so increment indegree of all vertices v that have an edge from u to v
            for (Edge e : g.incident(u)) {
                Vertex v = e.otherEnd(u);
                incrementInDegree(v);
            }
            // set visited to false
            setVisited(u, false);
        }

        @Override
        public void visit(Vertex[] arr, int k) {
            count++;
            if (print) {
                for (Vertex u : arr) {
                    System.out.print(u + " ");
                }
                System.out.println();
            }
        }

    }


    // To do: LP4; return the number of topological orders of g
    public long enumerateTopological(boolean flag) {

        // check if DAG. No topological order if not a DAG
        if (isDAG()) {return 0;}
        print = flag;

        Vertex[] arr = g.getVertexArray();

        Enumerate e = new Enumerate(arr, sel); // pass custom approver to enumerate
        initialise(); // initialise enum vertex
        e.permute(arr.length);

        return count;
    }

    // check for DAG
    private boolean isDAG() {
        return DFS.topologicalOrder1(g) == null;

    }

    // -------------------static methods----------------------

    public static long countTopologicalOrders(Graph g) {
        EnumerateTopological et = new EnumerateTopological(g);
        return et.enumerateTopological(false);
    }

    public static long enumerateTopologicalOrders(Graph g) {
        EnumerateTopological et = new EnumerateTopological(g);
        return et.enumerateTopological(true);
    }

    public static void main(String[] args) throws FileNotFoundException {
        int VERBOSE = 0;
//        String graph = "11 12   2 4 1   2 5 1   3 5 1   3 6 1   4 7 1   5 7 1   5 8 1   6 8 1   6 9 1   7 10 1   8 10 1   9 10 1      0 3 2 3 2 1 3 2 4 1 0";
		String graph = "4 3  1 3 1  2 4 1   3 4 1  0";
        if (args.length > 0) {
            VERBOSE = Integer.parseInt(args[0]);
        }
        // Graph g = Graph.readDirectedGraph(new java.util.Scanner(System.in));
        Scanner in;
        in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(graph);
        // in = new Scanner(new File("F:\\UT Dallas\\Courses\\Sem1-Fall18\\Implementation of Data Structures\\LP\\LP4\\EnumerareTopological\\permute-dag-10.txt"));
        Graph g = Graph.readDirectedGraph(in);
        DFS d = new DFS(g);

        Graph.Timer t = new Graph.Timer();

        long result;
        if (VERBOSE > 0) {
            result = enumerateTopologicalOrders(g);
        } else {
            result = countTopologicalOrders(g);
        }
        System.out.println("\n" + result + "\n" + t.end());
    }

}
