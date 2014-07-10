/**
 *  Desc:   Class to define an edge connecting two nodes.          
 * 
 *          An edge has an associated cost.
 * 
 * @author Petr (http://www.sallyx.org/)
 */
package common.Graph.GraphEdgeTypes;

import java.util.Scanner;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.InputStream;
import static common.Graph.NodeTypeEnumerations.invalid_node_index;

public class GraphEdge {

    //An edge connects two nodes. Valid node indices are always positive.
    protected int m_iFrom;
    protected int m_iTo;
    //the cost of traversing the edge
    protected double m_dCost;

    //ctors
    public GraphEdge(int from, int to, double cost) {
        m_dCost = cost;
        m_iFrom = from;
        m_iTo = to;
    }

    public GraphEdge(int from, int to) {
        m_dCost = 1.0;
        m_iFrom = from;
        m_iTo = to;
    }

    public GraphEdge() {
        m_dCost = 1.0;
        m_iFrom = invalid_node_index;
        m_iTo = invalid_node_index;
    }
    
    public GraphEdge(GraphEdge e) {
        this(e.From(),e.To(),e.Cost());
    }

    //stream constructor
    public GraphEdge(InputStream stream) throws IOException {
        Scanner buffer = new Scanner(stream);
        buffer.next();
        m_iFrom = buffer.nextInt();
        buffer.next();
        m_iTo = buffer.nextInt();
        buffer.next();
        m_dCost = buffer.nextDouble();
    }

    //for reading and writing to streams.
    public OutputStream print(OutputStream os) {
        PrintStream ps = new PrintStream(os);
        ps.print("m_iFrom: ");
        ps.print(m_iFrom);
        ps.print(" m_iTo: ");
        ps.print(m_iTo);
        ps.print(" m_dCost: ");
        ps.print(m_dCost);
        ps.println();
        return os;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public int From() {
        return m_iFrom;
    }

    public void SetFrom(int NewIndex) {
        m_iFrom = NewIndex;
    }

    public int To() {
        return m_iTo;
    }

    public void SetTo(int NewIndex) {
        m_iTo = NewIndex;
    }

    public double Cost() {
        return m_dCost;
    }

    public void SetCost(double NewCost) {
        m_dCost = NewCost;
    }

    //these two operators are required
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof GraphEdge)) {
            return false;
        }
        GraphEdge rhs = (GraphEdge) o;
        return rhs.m_iFrom == this.m_iFrom
                && rhs.m_iTo == this.m_iTo
                && rhs.m_dCost == this.m_dCost;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.m_iFrom;
        hash = 59 * hash + this.m_iTo;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.m_dCost) ^ (Double.doubleToLongBits(this.m_dCost) >>> 32));
        return hash;
    }
}
