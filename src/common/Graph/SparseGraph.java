/**
 * Desc: Graph class using the adjacency list representation.
 *
 * @author Petr (http://www.sallyx.org/)
 */
package common.Graph;

import static common.Graph.NodeTypeEnumerations.invalid_node_index;
import common.Graph.GraphEdgeTypes.GraphEdge;
import common.Graph.GraphNodeTypes.GraphNode;
import common.Graph.GraphNodeTypes.NavGraphNode;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Scanner;

public class SparseGraph<node_type extends NavGraphNode, edge_type extends GraphEdge> implements Graph {

    //enable easy client access to the edge and node types used in the graph
    //typedef edge_type                EdgeType;
    //typedef node_type                NodeType;
    //a couple more typedefs to save my fingers and to help with the formatting
    //of the code on the printed page
    public class NodeVector extends ArrayList<GraphNode> {

        public NodeVector() {
            super();
        }
    };

    public class EdgeList<edge_type> extends LinkedList<GraphEdge> {

        public EdgeList() {
            super();
        }
    };

    public class EdgeListVector extends ArrayList<EdgeList> {

        public EdgeListVector() {
            super();
        }
    };
    //the nodes that comprise this graph
    private NodeVector m_Nodes = new NodeVector();
    //a vector of adjacency edge lists. (each node index keys into the 
    //list of edges associated with that node)
    private EdgeListVector m_Edges = new EdgeListVector();
    //is this a directed graph?
    private boolean m_bDigraph;
    //the index of the next node to be added
    private int m_iNextNodeIndex;

    /**
     * @return true if the edge is not present in the graph. Used when adding
     * edges to prevent duplication
     */
    private boolean UniqueEdge(int from, int to) {
        ListIterator<edge_type> curEdge = m_Edges.get(from).listIterator();
        while (curEdge.hasNext()) {
            if (curEdge.next().To() == to) {
                return false;
            }
        }

        return true;
    }

    /**
     * iterates through all the edges in the graph and removes any that point to
     * an invalidated node
     */
    private void CullInvalidEdges() {
        ListIterator<EdgeList> edgeListIt = m_Edges.listIterator();
        while (edgeListIt.hasNext()) {
            EdgeList curEdgeList = edgeListIt.next();
            ListIterator<edge_type> edgeIt = curEdgeList.listIterator();
            while (edgeIt.hasNext()) {
                edge_type curEdge = edgeIt.next();
                if ((m_Nodes.get(curEdge.To())).Index() == invalid_node_index
                        || m_Nodes.get(curEdge.From()).Index() == invalid_node_index) {
                    curEdgeList.remove(curEdge);
                }
            }
        }
    }

    //ctor
    public SparseGraph(boolean digraph) {
        m_iNextNodeIndex = 0;
        m_bDigraph = digraph;
    }

    /**
     * method for obtaining a reference to a specific node
     *
     * @return the node at the given index
     */
    @Override
    public node_type GetNode(int idx) {
        assert (idx < (int) m_Nodes.size())
                && (idx >= 0) :
                "<SparseGraph::GetNode>: invalid index";

        return (node_type) m_Nodes.get(idx);
    }

    /**
     * const and non const methods for obtaining a reference to a specific edge
     */
    public edge_type GetEdge(int from, int to) {
        assert (from < m_Nodes.size())
                && (from >= 0)
                && m_Nodes.get(from).Index() != invalid_node_index :
                "<SparseGraph::GetEdge>: invalid 'from' index";

        assert (to < m_Nodes.size())
                && (to >= 0)
                && m_Nodes.get(to).Index() != invalid_node_index :
                "<SparseGraph::GetEdge>: invalid 'to' index";

        ListIterator<edge_type> it = m_Edges.get(from).listIterator();
        while (it.hasNext()) {
            edge_type curEdge = it.next();
            if (curEdge.To() == to) {
                return curEdge;
            }
        }

        assert false : "<SparseGraph::GetEdge>: edge does not exist";
        return null;
    }

    //retrieves the next free node index
    public int GetNextFreeNodeIndex() {
        return m_iNextNodeIndex;
    }

    /**
     * Given a node this method first checks to see if the node has been added
     * previously but is now innactive. If it is, it is reactivated.
     *
     * If the node has not been added previously, it is checked to make sure its
     * index matches the next node index before being added to the graph
     */
    public int AddNode(node_type node) {
        if (node.Index() < (int) m_Nodes.size()) {
            //make sure the client is not trying to add a node with the same ID as
            //a currently active node
            assert m_Nodes.get(node.Index()).Index() == invalid_node_index :
                    "<SparseGraph::AddNode>: Attempting to add a node with a duplicate ID";

            m_Nodes.set(node.Index(), node);

            return m_iNextNodeIndex;
        } else {
            //make sure the new node has been indexed correctly
            assert node.Index() == m_iNextNodeIndex : "<SparseGraph::AddNode>:invalid index";

            m_Nodes.add(node);
            m_Edges.add(new EdgeList());

            return m_iNextNodeIndex++;
        }
    }

    /**
     * Removes a node from the graph and removes any links to neighbouring nodes
     */
    public void RemoveNode(int node) {
        assert node < (int) m_Nodes.size() : "<SparseGraph::RemoveNode>: invalid node index";

        //set this node's index to invalid_node_index
        m_Nodes.get(node).SetIndex(invalid_node_index);

        //if the graph is not directed remove all edges leading to this node and then
        //clear the edges leading from the node
        if (!m_bDigraph) {
            //visit each neighbour and erase any edges leading to this node
            ListIterator<edge_type> it = m_Edges.get(node).listIterator();
            while (it.hasNext()) {
                edge_type curEdge = it.next();
                ListIterator<edge_type> itTo = m_Edges.get(curEdge.To()).listIterator();
                while (itTo.hasNext()) {
                    edge_type curE = itTo.next();
                    if (curE.To() == node) {
                        m_Edges.get(curEdge.To()).remove(curE);
                        break;
                    }
                }
            }

            //finally, clear this node's edges
            m_Edges.get(node).clear();
        } //if a digraph remove the edges the slow way
        else {
            CullInvalidEdges();
        }
    }

    /**
     * Use this to add an edge to the graph. The method will ensure that the
     * edge passed as a parameter is valid before adding it to the graph. If the
     * graph is a digraph then a similar edge connecting the nodes in the
     * opposite direction will be automatically added.
     */
    public void AddEdge(edge_type edge) {
        //first make sure the from and to nodes exist within the graph 
        assert (edge.From() < m_iNextNodeIndex) && (edge.To() < m_iNextNodeIndex) :
                "<SparseGraph::AddEdge>: invalid node index";

        //make sure both nodes are active before adding the edge
        if ((m_Nodes.get(edge.To()).Index() != invalid_node_index)
                && (m_Nodes.get(edge.From()).Index() != invalid_node_index)) {
            //add the edge, first making sure it is unique
            if (UniqueEdge(edge.From(), edge.To())) {
                m_Edges.get(edge.From()).add(edge);
            }

            //if the graph is undirected we must add another connection in the opposite
            //direction
            if (!m_bDigraph) {
                //check to make sure the edge is unique before adding
                if (UniqueEdge(edge.To(), edge.From())) {
                    edge_type NewEdge = null;
                    try {
                        NewEdge = (edge_type) edge.getClass().getConstructor(edge.getClass()).newInstance(edge);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    NewEdge.SetTo(edge.From());
                    NewEdge.SetFrom(edge.To());

                    m_Edges.get(edge.To()).add(NewEdge);
                }
            }
        }
    }

    /**
     * removes the edge connecting from and to from the graph (if present). If a
     * digraph then the edge connecting the nodes in the opposite direction will
     * also be removed.
     */
    public void RemoveEdge(int from, int to) {
        assert (from < (int) m_Nodes.size()) && (to < (int) m_Nodes.size()) :
                "<SparseGraph::RemoveEdge>:invalid node index";

        ListIterator<edge_type> it;

        if (!m_bDigraph) {
            it = m_Edges.get(to).listIterator();
            while (it.hasNext()) {
                edge_type curEdge = it.next();
                if (curEdge.To() == from) {
                    m_Edges.get(to).remove(curEdge);
                    break;
                }
            }
        }

        it = m_Edges.get(from).listIterator();
        while (it.hasNext()) {
            edge_type curEdge = it.next();
            if (curEdge.To() == to) {
                m_Edges.get(from).remove(curEdge);
                break;
            }
        }
    }

    /**
     * Sets the cost of a specific edge
     */
    public void SetEdgeCost(int from, int to, double NewCost) {
        //make sure the nodes given are valid
        assert (from < m_Nodes.size()) && (to < m_Nodes.size()) :
                "<SparseGraph::SetEdgeCost>: invalid index";

        //visit each neighbour and erase any edges leading to this node
        ListIterator<edge_type> it = m_Edges.get(from).listIterator();
        while (it.hasNext()) {
            edge_type curEdge = it.next();
            if (curEdge.To() == to) {
                curEdge.SetCost(NewCost);
                break;
            }
        }
    }

    /**
     * returns the number of active + inactive nodes present in the graph
     */
    public int NumNodes() {
        return m_Nodes.size();
    }

    /**
     * returns the number of active nodes present in the graph (this method's
     * performance can be improved greatly by caching the value)
     */
    public int NumActiveNodes() {
        int count = 0;

        for (int n = 0; n < m_Nodes.size(); ++n) {
            if (m_Nodes.get(n).Index() != invalid_node_index) {
                ++count;
            }
        }

        return count;
    }

    /**
     * returns the total number of edges present in the graph
     */
    public int NumEdges() {
        int tot = 0;

        ListIterator<EdgeList> curEdge = m_Edges.listIterator();
        while (curEdge.hasNext()) {
            tot += curEdge.next().size();
        }

        return tot;
    }

    /**
     * @return true if the graph is directed
     */
    public boolean isDigraph() {
        return m_bDigraph;
    }

    /**
     * @return true if the graph contains no nodes
     */
    public boolean isEmpty() {
        return m_Nodes.isEmpty();
    }

    /**
     * returns true if a node with the given index is present in the graph
     */
    public boolean isNodePresent(int nd) {
        if ((nd >= (int) m_Nodes.size() || (m_Nodes.get(nd).Index() == invalid_node_index))) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @return true if an edge with the given from/to is present in the graph
     */
    public boolean isEdgePresent(int from, int to) {
        if (isNodePresent(from) && isNodePresent(from)) {
            ListIterator<edge_type> curEdge = m_Edges.get(from).listIterator();
            while (curEdge.hasNext()) {
                if (curEdge.next().To() == to) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    //methods for loading and saving graphs from an open file stream or from
    //a file name 
//-------------------------------- Save ---------------------------------------
    public boolean Save(final String FileName) {
        //open the file and make sure it's valid
        OutputStream out;
        try {
            out = new FileOutputStream(FileName);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Cannot open file: " + FileName, ex);
        }

        return Save(out);
    }

//-------------------------------- Save ---------------------------------------
    public boolean Save(OutputStream stream) {
        PrintStream ps = new PrintStream(stream);
        //save the number of nodes
        ps.println(m_Nodes.size());

        //iterate through the graph nodes and save them
        ListIterator<GraphNode> curNode = m_Nodes.listIterator();
        while (curNode.hasNext()) {
            curNode.next().print(ps);
        }

        //save the number of edges
        ps.println(NumEdges());


        //iterate through the edges and save them
        for (int nodeIdx = 0; nodeIdx < m_Nodes.size(); ++nodeIdx) {
            ListIterator<edge_type> curEdge = m_Edges.get(nodeIdx).listIterator();
            while (curEdge.hasNext()) {
                curEdge.next().print(ps);
            }
        }

        return true;
    }

//------------------------------- Load ----------------------------------------
//-----------------------------------------------------------------------------
    public boolean Load(final String FileName, Class<? extends node_type> nodeCtor,
            Class<? extends edge_type> edgeCtor) {
        //open file and make sure it's valid
        InputStream in;
        try {
            in = new FileInputStream(FileName);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Cannot open file: " + FileName, ex);
        }

        return Load(new Scanner(in), nodeCtor, edgeCtor);
    }

//------------------------------- Load ----------------------------------------
//-----------------------------------------------------------------------------
    public boolean Load(Scanner in, Class<? extends node_type> nodeCtor,
            Class<? extends edge_type> edgeCtor) {
        Clear();

        //get the number of nodes and read them in
        int NumNodes, NumEdges;

        NumNodes = in.nextInt();

        node_type NewNode;
        for (int n = 0; n < NumNodes; ++n) {
            try {
                NewNode = nodeCtor.getConstructor(in.getClass()).newInstance(in);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            //when editing graphs it's possible to end up with a situation where some
            //of the nodes have been invalidated (their id's set to invalid_node_index). Therefore
            //when a node of index invalid_node_index is encountered, it must still be added.
            if (NewNode.Index() != invalid_node_index) {
                AddNode(NewNode);
            } else {
                m_Nodes.add(NewNode);

                //make sure an edgelist is added for each node
                m_Edges.add(new EdgeList());

                ++m_iNextNodeIndex;
            }
        }

        //now add the edges
        NumEdges = in.nextInt();
        for (int e = 0; e < NumEdges; ++e) {
            try {
                edge_type NextEdge = edgeCtor.getConstructor(in.getClass()).newInstance(in);
                AddEdge(NextEdge);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        return true;
    }

    /**
     * clears the graph ready for new node insertions
     */
    public void Clear() {
        m_iNextNodeIndex = 0;
        m_Nodes.clear();
        m_Edges.clear();
    }

    public void RemoveEdges() {
        ListIterator<EdgeList> it = m_Edges.listIterator();
        while (it.hasNext()) {
            it.next().clear();
        }
    }

    /**
     * non const class used to iterate through all the edges connected to a
     * specific node.
     */
    public static class EdgeIterator<node_type extends NavGraphNode, edge_type extends GraphEdge> {

        private ListIterator<edge_type> curEdge;
        private SparseGraph<node_type, edge_type> G;
        private final int NodeIndex;
        private boolean end = false;

        public EdgeIterator(SparseGraph<node_type, edge_type> graph,
                int node) {
            G = graph;
            NodeIndex = node;
            /* we don't need to check for an invalid node index since if the node is
             invalid there will be no associated edges
             */
            curEdge = G.m_Edges.get(NodeIndex).listIterator();
        }

        public edge_type begin() {
            curEdge = G.m_Edges.get(NodeIndex).listIterator();
            if (curEdge.hasNext()) {
                end = false;
                return curEdge.next();
            }
            end = true;
            return null;
        }

        public edge_type next() {
            if (!curEdge.hasNext()) {
                end = true;
                return null;
            }
            return curEdge.next();
        }

        //return true if we are at the end of the edge list
        public boolean end() {
            return end;
        }
    }
    //FAKE const class used to iterate through all the edges connected to a specific node. 

    public static class ConstEdgeIterator<node_type extends NavGraphNode, edge_type extends GraphEdge>
            extends EdgeIterator<node_type, edge_type> {

        public ConstEdgeIterator(SparseGraph<node_type, edge_type> graph,
                int node) {
            super(graph, node);
        }
    }

    /**
     * non const class used to iterate through the nodes in the graph
     */
    public static class NodeIterator<node_type extends NavGraphNode, edge_type extends GraphEdge> {

        private ListIterator<GraphNode> curNode;
        private SparseGraph<node_type, edge_type> G;
        private boolean end = false;
        //if a graph node is removed, it is not removed from the 
        //vector of nodes (because that would mean changing all the indices of 
        //all the nodes that have a higher index). This method takes a node
        //iterator as a parameter and assigns the next valid element to it.

        private GraphNode GetNextValidNode(ListIterator<GraphNode> it) {
            if (!curNode.hasNext()) {
                end = true;
                return null;
            }
            GraphNode itNode = curNode.next();
            if (itNode.Index() != invalid_node_index) {
                return itNode;
            }

            while ((itNode.Index() == invalid_node_index)) {
                if (!curNode.hasNext()) {
                    end = true;
                    return null;
                }
                itNode = curNode.next();
            }
            return itNode;
        }

        public NodeIterator(SparseGraph<node_type, edge_type> graph) {
            G = graph;
            curNode = G.m_Nodes.listIterator();
        }

        public node_type begin() {
            curNode = G.m_Nodes.listIterator();
            end = false;
            return (node_type) GetNextValidNode(curNode);
        }

        public node_type next() {
            if (!curNode.hasNext()) {
                end = true;
                return null;
            }

            return (node_type) GetNextValidNode(curNode);
        }

        public boolean end() {
            return end;
        }
    }

    //FAKE const class used to iterate through the nodes in the graph
    public static class ConstNodeIterator<node_type extends NavGraphNode, edge_type extends GraphEdge>
            extends NodeIterator<node_type, edge_type> {

        public ConstNodeIterator(SparseGraph<node_type, edge_type> graph) {
            super(graph);
        }
    }
}
