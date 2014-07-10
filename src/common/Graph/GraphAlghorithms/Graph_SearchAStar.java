/**
 *  this searchs a graph using the distance between the target node and the 
 *  currently considered node as a heuristic.
 *
 * This search is more commonly known as A* (pronounced Ay-Star)
 * 
 * @author Petr (http://www.sallyx.org/)
 */
package common.Graph.GraphAlghorithms;

import common.misc.PriorityQueue.IndexedPriorityQLow;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import common.Graph.GraphEdgeTypes.GraphEdge;
import java.util.ArrayList;
import common.Graph.SparseGraph;
import static common.Graph.AStarHeuristicPolicies.Heuristic;

//-----------------------------------------------------------------------------
public class Graph_SearchAStar<graph_type extends SparseGraph, ASHeuristic extends Heuristic> {

    //create a typedef for the edge type used by the graph
    //typedef typename graph_type::EdgeType Edge;
    private final graph_type m_Graph;
    private final ASHeuristic m_Heuristic;
    //indexed into my node. Contains the 'real' accumulative cost to that node
    private ArrayList<Double> m_GCosts;
    //indexed into by node. Contains the cost from adding m_GCosts[n] to
    //the heuristic cost from n to the target node. This is the vector the
    //iPQ indexes into.
    private ArrayList<Double> m_FCosts;
    private ArrayList<GraphEdge> m_ShortestPathTree;
    private ArrayList<GraphEdge> m_SearchFrontier;
    private int m_iSource;
    private int m_iTarget;

    public Graph_SearchAStar(graph_type graph, ASHeuristic heuristic,
            int source,
            int target) {
        m_Graph = graph;
        m_Heuristic = heuristic;
        m_ShortestPathTree = new ArrayList<GraphEdge>(Collections.nCopies(graph.NumNodes(), (GraphEdge) null));
        m_SearchFrontier = new ArrayList<GraphEdge>(Collections.nCopies(graph.NumNodes(), (GraphEdge) null));
        m_GCosts = new ArrayList<Double>(Collections.nCopies(graph.NumNodes(), 0.0));
        m_FCosts = new ArrayList<Double>(Collections.nCopies(graph.NumNodes(), 0.0));
        m_iSource = source;
        m_iTarget = target;
        Search();
    }

    /**
     * @return the vector of edges that the algorithm has examined
     */
    public ArrayList<GraphEdge> GetSPT() {
        return m_ShortestPathTree;
    }

    /**
     * @return a vector of node indexes that comprise the shortest path
     * from the source to the target
     */
    public List<Integer> GetPathToTarget() {
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

    //returns the total cost to the target
    public double GetCostToTarget() {
        return m_GCosts.get(m_iTarget);
    }

    /**
     * the A* search algorithm
     */
    private void Search() {
        //create an indexed priority queue of nodes. The nodes with the
        //lowest overall F cost (G+H) are positioned at the front.
        IndexedPriorityQLow<Double> pq = new IndexedPriorityQLow(m_FCosts, m_Graph.NumNodes());

        //put the source node on the queue
        pq.insert(m_iSource);

        //while the queue is not empty
        while (!pq.empty()) {
            //get lowest cost node from the queue
            int NextClosestNode = pq.Pop();

            //move this node from the frontier to the spanning tree
            m_ShortestPathTree.set(NextClosestNode, m_SearchFrontier.get(NextClosestNode));

            //if the target has been found exit
            if (NextClosestNode == m_iTarget) {
                return;
            }

            //now to test all the edges attached to this node
            graph_type.EdgeIterator ConstEdgeItr = new graph_type.EdgeIterator(m_Graph, NextClosestNode);

            for (GraphEdge pE = ConstEdgeItr.begin();
                    !ConstEdgeItr.end();
                    pE = ConstEdgeItr.next()) {
                //calculate the heuristic cost from this node to the target (H)                       
                double HCost = m_Heuristic.Calculate(m_Graph, m_iTarget, pE.To());

                //calculate the 'real' cost to this node from the source (G)
                double GCost = m_GCosts.get(NextClosestNode) + pE.Cost();

                //if the node has not been added to the frontier, add it and update
                //the G and F costs
                if (m_SearchFrontier.get(pE.To()) == null) {
                    m_FCosts.set(pE.To(), GCost + HCost);
                    m_GCosts.set(pE.To(), GCost);

                    pq.insert(pE.To());

                    m_SearchFrontier.set(pE.To(), pE);
                } //if this node is already on the frontier but the cost to get here
                //is cheaper than has been found previously, update the node
                //costs and frontier accordingly.
                else if ((GCost < m_GCosts.get(pE.To())) && (m_ShortestPathTree.get(pE.To()) == null)) {
                    m_FCosts.set(pE.To(), GCost + HCost);
                    m_GCosts.set(pE.To(), GCost);

                    pq.ChangePriority(pE.To());

                    m_SearchFrontier.set(pE.To(), pE);
                }
            }
        }
    }
}