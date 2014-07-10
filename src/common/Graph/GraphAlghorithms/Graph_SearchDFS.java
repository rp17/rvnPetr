/**
 * class to implement a depth first search.
 *
 * @author Petr (http://www.sallyx.org/)
 */
package common.Graph.GraphAlghorithms;

import common.Graph.GraphEdgeTypes.GraphEdge;
import common.Graph.SparseGraph;
import java.util.*;

//  class to implement a depth first search. 
public class Graph_SearchDFS<graph_type extends SparseGraph> {
    //to aid legibility

    private final int visited = 0;
    private final int unvisited = 1;
    private final int no_parent_assigned = 2;
    //create a typedef for the edge and node types used by the graph
    //typedef typename graph_type::EdgeType Edge;
    //typedef typename graph_type::NodeType Node;
    //a reference to the graph to be searched
    private final graph_type m_Graph;
    //this records the indexes of all the nodes that are visited as the
    //search progresses
    private ArrayList<Integer> m_Visited;
    //this holds the route taken to the target. Given a node index, the value
    //at that index is the node's parent. ie if the path to the target is
    //3-8-27, then m_Route[8] will hold 3 and m_Route[27] will hold 8.
    private ArrayList<Integer> m_Route;
    //As the search progresses, this will hold all the edges the algorithm has
    //examined. THIS IS NOT NECESSARY FOR THE SEARCH, IT IS HERE PURELY
    //TO PROVIDE THE USER WITH SOME VISUAL FEEDBACK
    private ArrayList<GraphEdge> m_SpanningTree = new ArrayList<GraphEdge>();
    //the source and target node indices
    private int m_iSource,
            m_iTarget;
    //true if a path to the target has been found
    private boolean m_bFound;

    public Graph_SearchDFS(final graph_type graph,
            int source) {
        this(graph, source, -1);
    }

    public Graph_SearchDFS(final graph_type graph,
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

    //returns a vector containing pointers to all the edges the search has examined
    public ArrayList<? extends GraphEdge> GetSearchTree() {
        return m_SpanningTree;
    }

    //returns true if the target node has been located
    public boolean Found() {
        return m_bFound;
    }

    /**
     * this method performs the DFS search
     */
    private boolean Search() {
        //create a std stack of edges
        Stack<GraphEdge> stack = new Stack<GraphEdge>();

        //create a dummy edge and put on the stack
        GraphEdge Dummy = new GraphEdge(m_iSource, m_iSource, 0);

        stack.push(Dummy);

        //while there are edges in the stack keep searching
        while (!stack.empty()) {
            //grab the next edge
            final GraphEdge Next = stack.peek();

            //remove the edge from the stack
            stack.pop();

            //make a note of the parent of the node this edge points to
            m_Route.set(Next.To(), Next.From());

            //put it on the tree. (making sure the dummy edge is not placed on the tree)
            if (Next != Dummy) {
                m_SpanningTree.add(Next);
            }

            //and mark it visited
            m_Visited.set(Next.To(), visited);

            //if the target has been found the method can return success
            if (Next.To() == m_iTarget) {
                return true;
            }

            //push the edges leading from the node this edge points to onto
            //the stack (provided the edge does not point to a previously 
            //visited node)
            SparseGraph.EdgeIterator ConstEdgeItr = new SparseGraph.EdgeIterator(m_Graph, Next.To());

            for (GraphEdge pE = ConstEdgeItr.begin();
                    !ConstEdgeItr.end();
                    pE = ConstEdgeItr.next()) {
                if (m_Visited.get(pE.To()) == unvisited) {
                    stack.push(pE);
                }
            }
        }

        //no path to target
        return false;
    }

    /**
     * returns a vector of node indexes that comprise the shortest path from the
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
