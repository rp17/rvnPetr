/**
 *  Given a graph, source and optional target this class solves for
 *  single source shortest paths (without a target being specified) or 
 *  shortest path from source to target.
 *
 *  The algorithm used is a priority queue implementation of Dijkstra's.
 *  note how similar this is to the algorithm used in Graph_MinSpanningTree.
 *  The main difference is in the calculation of the priority in the line:
 *  
 *  double NewCost = m_CostToThisNode[best] + pE->Cost;
 *
 * @author Petr (http://www.sallyx.org/)
 */
package common.Graph.GraphAlghorithms;

import common.Graph.GraphEdgeTypes.GraphEdge;
import common.Graph.SparseGraph;
import common.misc.PriorityQueue.IndexedPriorityQLow;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Graph_SearchDijkstra<graph_type extends SparseGraph> {
    //create a typedef for the edge type used by the graph
    //typedef typename graph_type::EdgeType Edge;

    private final graph_type m_Graph;
    //this vector contains the edges that comprise the shortest path tree -
    //a directed subtree of the graph that encapsulates the best paths from 
    //every node on the SPT to the source node.
    private ArrayList<GraphEdge> m_ShortestPathTree;
    //this is indexed into by node index and holds the total cost of the best
    //path found so far to the given node. For example, m_CostToThisNode[5]
    //will hold the total cost of all the edges that comprise the best path
    //to node 5, found so far in the search (if node 5 is present and has 
    //been visited)
    private ArrayList<Double> m_CostToThisNode;
    //this is an indexed (by node) vector of 'parent' edges leading to nodes 
    //connected to the SPT but that have not been added to the SPT yet. This is
    //a little like the stack or queue used in BST and DST searches.
    private ArrayList<GraphEdge> m_SearchFrontier;
    private int m_iSource;
    private int m_iTarget;

    public Graph_SearchDijkstra(final graph_type graph,
            int source) {
        this(graph, source, -1);
    }

    public Graph_SearchDijkstra(final graph_type graph,
            int source,
            int target) {
        m_Graph = graph;
        m_ShortestPathTree = new ArrayList<GraphEdge>(Collections.nCopies(graph.NumNodes(), (GraphEdge) null));
        m_SearchFrontier = new ArrayList<GraphEdge>(Collections.nCopies(graph.NumNodes(), (GraphEdge) null));
        m_CostToThisNode = new ArrayList<Double>(Collections.nCopies(graph.NumNodes(), 0.0));

        m_iSource = source;
        m_iTarget = target;

        Search();
    }

    /**
     * returns the vector of edges that defines the SPT. If a target was given
     * in the constructor then this will be an SPT comprising of all the nodes
     * examined before the target was found, else it will contain all the nodes
     * in the graph.
     */
    public ArrayList<GraphEdge> GetSPT() {
        return m_ShortestPathTree;
    }

    /**
     * returns a vector of node indexes that comprise the shortest path
     * from the source to the target. It calculates the path by working
     * backwards through the SPT from the target node.
     */
    public LinkedList<Integer> GetPathToTarget() {
        LinkedList<Integer> path = new LinkedList<Integer>();

        //just return an empty path if no target or no path found
        if (m_iTarget < 0) {
            return path;
        }

        int nd = m_iTarget;

        path.addFirst(nd);

        while ((nd != m_iSource) && (m_ShortestPathTree.get(nd) != null)) {
            nd = m_ShortestPathTree.get(nd).From();

            path.addFirst(nd);
        }

        return path;
    }

    /**
     * returns the total cost to the target
     */
    public double GetCostToTarget() {
        return m_CostToThisNode.get(m_iTarget);
    }

    //returns the total cost to the given node
    public double GetCostToNode(int nd) {
        return m_CostToThisNode.get(nd);
    }

    private void Search() {
        //create an indexed priority queue that sorts smallest to largest
        //(front to back).Note that the maximum number of elements the iPQ
        //may contain is N. This is because no node can be represented on the 
        //queue more than once.
        IndexedPriorityQLow<Double> pq = new IndexedPriorityQLow<Double>(m_CostToThisNode, m_Graph.NumNodes());

        //put the source node on the queue
        pq.insert(m_iSource);

        //while the queue is not empty
        while (!pq.empty()) {
            //get lowest cost node from the queue. Don't forget, the return value
            //is a *node index*, not the node itself. This node is the node not already
            //on the SPT that is the closest to the source node
            int NextClosestNode = pq.Pop();

            //move this edge from the frontier to the shortest path tree
            m_ShortestPathTree.set(NextClosestNode, m_SearchFrontier.get(NextClosestNode));

            //if the target has been found exit
            if (NextClosestNode == m_iTarget) {
                return;
            }

            //now to relax the edges.
            graph_type.EdgeIterator ConstEdgeItr = new graph_type.EdgeIterator(m_Graph, NextClosestNode);

            //for each edge connected to the next closest node
            for (GraphEdge pE = ConstEdgeItr.begin();
                    !ConstEdgeItr.end();
                    pE = ConstEdgeItr.next()) {
                //the total cost to the node this edge points to is the cost to the
                //current node plus the cost of the edge connecting them.
                double NewCost = m_CostToThisNode.get(NextClosestNode) + pE.Cost();

                //if this edge has never been on the frontier make a note of the cost
                //to get to the node it points to, then add the edge to the frontier
                //and the destination node to the PQ.
                if (m_SearchFrontier.get(pE.To()) == null) {
                    m_CostToThisNode.set(pE.To(), NewCost);

                    pq.insert(pE.To());

                    m_SearchFrontier.set(pE.To(), pE);
                } //else test to see if the cost to reach the destination node via the
                //current node is cheaper than the cheapest cost found so far. If
                //this path is cheaper, we assign the new cost to the destination
                //node, update its entry in the PQ to reflect the change and add the
                //edge to the frontier
                else if ((NewCost < m_CostToThisNode.get(pE.To()))
                        && (m_ShortestPathTree.get(pE.To()) == null)) {
                    m_CostToThisNode.set(pE.To(), NewCost);

                    //because the cost is less than it was previously, the PQ must be
                    //re-sorted to account for this.
                    pq.ChangePriority(pE.To());

                    m_SearchFrontier.set(pE.To(), pE);
                }
            }
        }
    }
}