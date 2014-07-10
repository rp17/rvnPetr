/**
 * @author Petr (http://www.sallyx.org/)
 */
package common.Graph.GraphAlghorithms;

import common.Graph.GraphEdgeTypes.GraphEdge;
import common.Graph.GraphNodeTypes.GraphNode;
import common.Graph.GraphNodeTypes.NavGraphNode;
import common.Graph.SparseGraph;
import java.util.*;

public class Graph_SearchBFS<graph_type extends SparseGraph> {
    //to aid legibility

    private final int visited = 0;
    private final int unvisited = 1;
    private final int no_parent_assigned = 2;
    //create a typedef for the edge type used by the graph
    //typedef typename graph_type::EdgeType Edge;
    //a reference to the graph to be searched
    private final graph_type m_Graph;
    //this records the indexes of all the nodes that are visited as the
    //search progresses
    private ArrayList<Integer> m_Visited = new ArrayList<Integer>();
    //this holds the route taken to the target. Given a node index, the value
    //at that index is the node's parent. ie if the path to the target is
    //3-8-27, then m_Route[8] will hold 3 and m_Route[27] will hold 8.
    private ArrayList<Integer> m_Route = new ArrayList<Integer>();
    //the source and target node indices
    private int m_iSource,
            m_iTarget;
    //true if a path to the target has been found
    private boolean m_bFound;
    //As the search progresses, this will hold all the edges the algorithm has
    //examined. THIS IS NOT NECESSARY FOR THE SEARCH, IT IS HERE PURELY
    //TO PROVIDE THE USER WITH SOME VISUAL FEEDBACK
    private ArrayList<GraphEdge> m_SpanningTree = new ArrayList<GraphEdge>();

    public Graph_SearchBFS(final graph_type graph,
            int source) {
        this(graph, source, -1);
    }

    public Graph_SearchBFS(final graph_type graph,
            int source,
            int target) {
        m_Graph = graph;
        m_iSource = source;
        m_iTarget = target;
        m_bFound = false;
        m_Visited = new ArrayList<Integer>(Collections.nCopies(m_Graph.NumNodes(), unvisited));
        m_Route = new ArrayList<Integer>(Collections.nCopies(m_Graph.NumNodes(), no_parent_assigned));
        m_bFound = Search();
    }

    public boolean Found() {
        return m_bFound;
    }

    //returns a vector containing pointers to all the edges the search has examined
    public ArrayList<GraphEdge> GetSearchTree() {
        return m_SpanningTree;
    }

    //the BFS algorithm is very similar to the DFS except that it uses a
    //FIFO queue instead of a stack.
    private boolean Search() {
        //create a std queue of edges
        Queue<GraphEdge> Q = new LinkedList<GraphEdge>();

        final GraphEdge Dummy = new GraphEdge(m_iSource, m_iSource, 0);

        //create a dummy edge and put on the queue
        Q.add(Dummy);

        //mark the source node as visited
        m_Visited.set(m_iSource, visited);

        //while there are edges in the queue keep searching
        while (!Q.isEmpty()) {
            //grab the next edge
            final GraphEdge Next = Q.peek();

            Q.remove();

            //mark the parent of this node
            m_Route.set(Next.To(), Next.From());

            //put it on the tree. (making sure the dummy edge is not placed on the tree)
            if (Next != Dummy) {
                m_SpanningTree.add(Next);
            }

            //exit if the target has been found
            if (Next.To() == m_iTarget) {
                return true;
            }

            //push the edges leading from the node at the end of this edge 
            //onto the queue
            SparseGraph.EdgeIterator<NavGraphNode, GraphEdge> ConstEdgeItr = new SparseGraph.EdgeIterator(m_Graph, Next.To());

            for (GraphEdge pE = ConstEdgeItr.begin();
                    !ConstEdgeItr.end();
                    pE = ConstEdgeItr.next()) {
                //if the node hasn't already been visited we can push the
                //edge onto the queue
                if (m_Visited.get(pE.To()) == unvisited) {
                    Q.add(pE);

                    //and mark it visited
                    m_Visited.set(pE.To(), visited);
                }
            }
        }

        //no path to target
        return false;
    }

    /**
     * @return a vector of node indexes that comprise the shortest path from the
     * source to the target
     */
    public List<Integer> GetPathToTarget() {
        LinkedList<Integer> path = new LinkedList<Integer>();

        //just return an empty path if no path to target found or if
        //no target has been specified
        if (!m_bFound || m_iTarget < 0) {
            return path;
        }

        int nd = m_iTarget;

        path.addFirst(nd);

        while (nd != m_iSource) {
            nd = m_Route.get(nd);
            path.addFirst(nd);
        }
        return path;
    }
}
