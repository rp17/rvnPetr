/**
 * given a graph and a source node you can use this class to calculate
 * the minimum spanning tree. If no source node is specified then the 
 * algorithm will calculate a spanning forest starting from node 1 
 *
 * It uses a priority first queue implementation of Prims algorithm
 * 
 * @author Petr (http://www.sallyx.org/)
 */
package common.Graph.GraphAlghorithms;

import common.Graph.GraphEdgeTypes.GraphEdge;
import common.Graph.SparseGraph;
import common.misc.PriorityQueue.IndexedPriorityQLow;
import java.util.ArrayList;
import java.util.Collections;

class Graph_MinSpanningTree<graph_type extends SparseGraph> {
    //create a typedef for the edge type used by the graph
    //typedef typename graph_type::EdgeType Edge;

    private final graph_type m_Graph;
    private ArrayList<Double> m_CostToThisNode;
    private ArrayList<GraphEdge> m_SpanningTree;
    private ArrayList<GraphEdge> m_Fringe;

    private void Search(final int source) {
        //create a priority queue
        IndexedPriorityQLow<Double> pq = new IndexedPriorityQLow<Double>(m_CostToThisNode, m_Graph.NumNodes());

        //put the source node on the queue
        pq.insert(source);

        //while the queue is not empty
        while (!pq.empty()) {
            //get lowest cost edge from the queue
            int best = pq.Pop();

            //move this edge from the fringe to the spanning tree
            m_SpanningTree.set(best, m_Fringe.get(best));

            //now to test the edges attached to this node
            graph_type.EdgeIterator ConstEdgeItr = new graph_type.EdgeIterator(m_Graph, best);

            for (GraphEdge pE = ConstEdgeItr.begin(); !ConstEdgeItr.end(); pE = ConstEdgeItr.next()) {
                double Priority = pE.Cost();

                if (m_Fringe.get(pE.To()) == null) {
                    m_CostToThisNode.set(pE.To(), Priority);

                    pq.insert(pE.To());

                    m_Fringe.add(pE.To(), pE);
                } else if ((Priority < m_CostToThisNode.get(pE.To())) && (m_SpanningTree.get(pE.To()) == null)) {
                    m_CostToThisNode.set(pE.To(), Priority);

                    pq.ChangePriority(pE.To());

                    m_Fringe.set(pE.To(), pE);
                }
            }
        }
    }

    public Graph_MinSpanningTree(graph_type G) {
        this(G, -1);
    }

    public Graph_MinSpanningTree(graph_type G, int source) {
        m_Graph = G;
        m_SpanningTree = new ArrayList<GraphEdge>(Collections.nCopies(G.NumNodes(), (GraphEdge) null));
        m_Fringe = new ArrayList<GraphEdge>(Collections.nCopies(G.NumNodes(), (GraphEdge) null));
        m_CostToThisNode = new ArrayList<Double>(Collections.nCopies(G.NumNodes(), -1.0));

        if (source < 0) {
            for (int nd = 0; nd < G.NumNodes(); ++nd) {
                if (m_SpanningTree.get(nd) == null) {
                    Search(nd);
                }
            }
        } else {
            Search(source);
        }
    }

    public ArrayList<GraphEdge> GetSpanningTree() {
        return m_SpanningTree;
    }
}
