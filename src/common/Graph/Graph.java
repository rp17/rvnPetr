/*
 * 
 *  @author Petr (http://www.sallyx.org/)
 */
package common.Graph;

import common.Graph.GraphEdgeTypes.GraphEdge;
import common.Graph.GraphNodeTypes.NavGraphNode;

/**
 *
 * @author Petr
 */
public interface Graph<node_type extends NavGraphNode, edge_type extends GraphEdge> {
    /**
     * method for obtaining a reference to a specific node
     * @return the node at the given index
     */
    public node_type GetNode(int idx);
    
    /**
     * returns the number of active + inactive nodes present in the graph
     */
    public int NumNodes();
}
