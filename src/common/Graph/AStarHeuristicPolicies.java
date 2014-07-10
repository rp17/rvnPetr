/**
 * Desc:   class templates defining a heuristic policy for use with the A*
 *         search algorithm
 * 
 * @author Petr (http://www.sallyx.org/)
 */
package common.Graph;

import common.Graph.GraphNodeTypes.NavGraphNode;
import common.Graph.GraphEdgeTypes.NavGraphEdge;
import static common.D2.Vector2D.Vec2DDistance;
import static common.misc.utils.RandInRange;

public class AStarHeuristicPolicies {

    public static interface Heuristic {
        public <graph_type extends SparseGraph<? extends NavGraphNode, ? extends NavGraphEdge>>
                double Calculate(final graph_type G, int nd1, int nd2);
        }
    /**
     * the euclidian heuristic (straight-line distance)
     */
    public static class Heuristic_Euclid implements Heuristic {

        public Heuristic_Euclid() {
        }

        //calculate the straight line distance from node nd1 to node nd2
        public <graph_type extends SparseGraph<? extends NavGraphNode, ? extends NavGraphEdge>>
                double Calculate(final graph_type G, int nd1, int nd2) {
            return Vec2DDistance(G.GetNode(nd1).Pos(), G.GetNode(nd2).Pos());
        }
    }

    /**
     * this uses the euclidian distance but adds in an amount of noise to the 
     * result. You can use this heuristic to provide imperfect paths. This can
     * be handy if you find that you frequently have lots of agents all following
     * each other in single file to get from one place to another
     */
    public static class Heuristic_Noisy_Euclidian implements Heuristic {

        public Heuristic_Noisy_Euclidian() {
        }

        //calculate the straight line distance from node nd1 to node nd2
        public <graph_type extends SparseGraph<? extends NavGraphNode, ? extends NavGraphEdge>>
                double Calculate(final graph_type G, int nd1, int nd2) {
            return Vec2DDistance(G.GetNode(nd1).Pos(), G.GetNode(nd2).Pos()) * RandInRange(0.9f, 1.1f);
        }
    }

    /**
     * you can use this class to turn the A* algorithm into Dijkstra's search.
     * this is because Dijkstra's is equivalent to an A* search using a heuristic
     * value that is always equal to zero.
     */
    public static class Heuristic_Dijkstra implements Heuristic {
        public Heuristic_Dijkstra() {
        }
        public <graph_type extends SparseGraph<? extends NavGraphNode, ? extends NavGraphEdge>>
                double Calculate(final graph_type G, int nd1, int nd2) {
            return 0;
        }
    }
}