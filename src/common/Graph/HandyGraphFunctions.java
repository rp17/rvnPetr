/**
 *  Desc:  As the name implies, some useful functions you can use with your
 *         graphs. 

 *         For the function templates, make sure your graph interface complies
 *         with the SparseGraph class
 * 
 * @author Petr (http://www.sallyx.org/)
 */
package common.Graph;

import common.Graph.GraphAlghorithms.Graph_SearchDijkstra;
import java.util.Collections;
import java.util.ArrayList;
import common.Graph.GraphNodeTypes.GraphNode;
import common.D2.Vector2D;
import static common.D2.Vector2D.*;
import common.Graph.GraphEdgeTypes.GraphEdge;
import common.Graph.GraphNodeTypes.NavGraphNode;
import static common.misc.Cgdi.gdi;
import static common.misc.Stream_Utility_function.ttos;
import static common.misc.utils.MinDouble;

public class HandyGraphFunctions {

    /**
     * @return true if x,y is a valid position in the map
     */
    public static boolean ValidNeighbour(int x, int y, int NumCellsX, int NumCellsY) {
        return !((x < 0) || (x >= NumCellsX) || (y < 0) || (y >= NumCellsY));
    }

    /**
     *  use to add he eight neighboring edges of a graph node that 
     *  is positioned in a grid layout
     */
    public static <graph_type extends SparseGraph> void GraphHelper_AddAllNeighboursToGridNode(graph_type graph,
            int row,
            int col,
            int NumCellsX,
            int NumCellsY) {
        for (int i = -1; i < 2; ++i) {
            for (int j = -1; j < 2; ++j) {
                int nodeX = col + j;
                int nodeY = row + i;

                //skip if equal to this node
                if ((i == 0) && (j == 0)) {
                    continue;
                }

                //check to see if this is a valid neighbour
                if (ValidNeighbour(nodeX, nodeY, NumCellsX, NumCellsY)) {
                    //calculate the distance to this node
                    Vector2D PosNode = graph.GetNode(row * NumCellsX + col).Pos();
                    Vector2D PosNeighbour = graph.GetNode(nodeY * NumCellsX + nodeX).Pos();

                    double dist = PosNode.Distance(PosNeighbour);

                    //this neighbour is okay so it can be added
                    GraphEdge NewEdge = new GraphEdge(row * NumCellsX + col,
                            nodeY * NumCellsX + nodeX,
                            dist);
                    graph.AddEdge(NewEdge);

                    //if graph is not a diagraph then an edge needs to be added going
                    //in the other direction
                    if (!graph.isDigraph()) {
                        NewEdge = new GraphEdge(nodeY * NumCellsX + nodeX,
                                row * NumCellsX + col,
                                dist);
                        graph.AddEdge(NewEdge);
                    }
                }
            }
        }
    }

    /**
     * creates a graph based on a grid layout. This function requires the 
     * dimensions of the environment and the number of cells required horizontally
     * and vertically 
     */
    public static <graph_type extends SparseGraph> void GraphHelper_CreateGrid(graph_type graph,
            int cySize,
            int cxSize,
            int NumCellsY,
            int NumCellsX) {
        //need some temporaries to help calculate each node center
        double CellWidth = (double) cySize / (double) NumCellsX;
        double CellHeight = (double) cxSize / (double) NumCellsY;

        double midX = CellWidth / 2;
        double midY = CellHeight / 2;


        //first create all the nodes
        for (int row = 0; row < NumCellsY; ++row) {
            for (int col = 0; col < NumCellsX; ++col) {
                graph.AddNode(new NavGraphNode<>(graph.GetNextFreeNodeIndex(),
                        new Vector2D(midX + (col * CellWidth),
                        midY + (row * CellHeight))));

            }
        }
        //now to calculate the edges. (A position in a 2d array [x][y] is the
        //same as [y*NumCellsX + x] in a 1d array). Each cell has up to eight
        //neighbours.
        for (int row = 0; row < NumCellsY; ++row) {
            for (int col = 0; col < NumCellsX; ++col) {
                GraphHelper_AddAllNeighboursToGridNode(graph, row, col, NumCellsX, NumCellsY);
            }
        }
    }

    /**
     *  draws a graph using the GDI
     */
    public static <graph_type extends SparseGraph> void GraphHelper_DrawUsingGDI(final graph_type graph, int color) {
        GraphHelper_DrawUsingGDI(graph, color, false);
    }

    /**
     *  draws a graph using the GDI
     */
    public static <graph_type extends SparseGraph> void GraphHelper_DrawUsingGDI(final graph_type graph, int color, boolean DrawNodeIDs) {

        //just return if the graph has no nodes
        if (graph.NumNodes() == 0) {
            return;
        }

        gdi.SetPenColor(color);

        //draw the nodes 
        graph_type.NodeIterator NodeItr = new graph_type.NodeIterator(graph);
        for (NavGraphNode pN = NodeItr.begin();
                !NodeItr.end();
                pN = NodeItr.next()) {
            gdi.Circle(pN.Pos(), 2);

            if (DrawNodeIDs) {
                gdi.TextColor(200, 200, 200);
                gdi.TextAtPos((int) pN.Pos().x + 5, (int) pN.Pos().y - 5, ttos(pN.Index()));
            }

            graph_type.EdgeIterator EdgeItr = new graph_type.EdgeIterator(graph, pN.Index());
            for (GraphEdge pE = EdgeItr.begin();
                    !EdgeItr.end();
                    pE = EdgeItr.next()) {
                gdi.Line(pN.Pos(), graph.GetNode(pE.To()).Pos());
            }
        }
    }

    /**
     * Given a cost value and an index to a valid node this function examines 
     * all a node's edges, calculates their length, and multiplies
     * the value with the weight. Useful for setting terrain costs.
     */
    public static <graph_type extends SparseGraph> 
            void WeightNavGraphNodeEdges(graph_type graph, int node, double weight) {
        //make sure the node is present
        assert (node < graph.NumNodes());

        //set the cost for each edge
        graph_type.EdgeIterator ConstEdgeItr = new graph_type.EdgeIterator(graph, node);
        for (GraphEdge pE = ConstEdgeItr.begin();
                !ConstEdgeItr.end();
                pE = ConstEdgeItr.next()) {
            //calculate the distance between nodes
            double dist = Vec2DDistance(graph.GetNode(pE.From()).Pos(),
                    graph.GetNode(pE.To()).Pos());

            //set the cost of this edge
            graph.SetEdgeCost(pE.From(), pE.To(), dist * weight);

            //if not a digraph, set the cost of the parallel edge to be the same
            if (!graph.isDigraph()) {
                graph.SetEdgeCost(pE.To(), pE.From(), dist * weight);
            }
        }
    }

    /**
     * creates a lookup table encoding the shortest path info between each node
     * in a graph to every other
     */
    public static <graph_type extends SparseGraph> ArrayList<ArrayList<Integer>>
            CreateAllPairsTable(final graph_type G) {
        final int no_path = -1;

        ArrayList<ArrayList<Integer>> ShortestPaths = 
                new ArrayList<ArrayList<Integer>>(Collections.nCopies(G.NumNodes(), 
                    new ArrayList<Integer>(Collections.nCopies(G.NumNodes(), no_path))));

        for (int source = 0; source < G.NumNodes(); ++source) {
            //calculate the SPT for this node
            Graph_SearchDijkstra<graph_type> search = new Graph_SearchDijkstra<graph_type>(G, source);

            ArrayList<GraphEdge> spt = search.GetSPT();

            //now we have the SPT it's easy to work backwards through it to find
            //the shortest paths from each node to this source node
            for (int target = 0; target < G.NumNodes(); ++target) {
                //if the source node is the same as the target just set to target
                if (source == target) {
                    ShortestPaths.get(source).set(target, target);
                } else {
                    int nd = target;

                    while ((nd != source) && (spt.get(nd) != null)) {
                        ShortestPaths.get(spt.get(nd).From()).set(target, nd);

                        nd = spt.get(nd).From();
                    }
                }
            }//next target node
        }//next source node

        return ShortestPaths;
    }

    /**
     * creates a lookup table of the cost associated from traveling from one
     * node to every other
     */
    public static <graph_type extends SparseGraph> ArrayList<ArrayList<Double>> 
            CreateAllPairsCostsTable(final graph_type G) {
        //create a two dimensional vector
        ArrayList<ArrayList<Double>> PathCosts =
                new ArrayList<ArrayList<Double>>(Collections.nCopies(G.NumNodes(), 
                    new ArrayList<Double>(Collections.nCopies(G.NumNodes(), 0.0))));

        for (int source = 0; source < G.NumNodes(); ++source) {
            //do the search
            Graph_SearchDijkstra<graph_type> search = new Graph_SearchDijkstra<graph_type>(G, source);

            //iterate through every node in the graph and grab the cost to travel to
            //that node
            for (int target = 0; target < G.NumNodes(); ++target) {
                if (source != target) {
                    PathCosts.get(source).set(target, search.GetCostToNode(target));
                }
            }//next target node

        }//next source node

        return PathCosts;
    }

    /**
     * determines the average length of the edges in a navgraph (using the 
     * distance between the source & target node positions (not the cost of the 
     * edge as represented in the graph, which may account for all sorts of 
     * other factors such as terrain type, gradients etc)
     */
    public static <graph_type extends SparseGraph>
            double CalculateAverageGraphEdgeLength(final graph_type G) {
        double TotalLength = 0;
        int NumEdgesCounted = 0;

        graph_type.NodeIterator NodeItr = new graph_type.NodeIterator(G);
        GraphNode pN;
        for (pN = NodeItr.begin(); !NodeItr.end(); pN = NodeItr.next()) {
            graph_type.EdgeIterator EdgeItr = new graph_type.EdgeIterator(G, pN.Index());
            for (GraphEdge pE = EdgeItr.begin(); !EdgeItr.end(); pE = EdgeItr.next()) {
                //increment edge counter
                ++NumEdgesCounted;

                //add length of edge to total length
                TotalLength += Vec2DDistance(G.GetNode(pE.From()).Pos(), G.GetNode(pE.To()).Pos());
            }
        }

        return TotalLength / (double) NumEdgesCounted;
    }

    /**
     *  @return the cost of the costliest edge in the graph
     */
    public static <graph_type extends SparseGraph> 
            double GetCostliestGraphEdge(final graph_type G) {
        double greatest = MinDouble;

        graph_type.NodeIterator NodeItr = new graph_type.NodeIterator(G);
        GraphNode pN;
        for (pN = NodeItr.begin(); !NodeItr.end(); pN = NodeItr.next()) {
            graph_type.EdgeIterator EdgeItr = new graph_type.EdgeIterator(G, pN.Index());
            for (GraphEdge pE = EdgeItr.begin(); !EdgeItr.end(); pE = EdgeItr.next()) {
                if (pE.Cost() > greatest) {
                    greatest = pE.Cost();
                }
            }
        }

        return greatest;
    }
}
