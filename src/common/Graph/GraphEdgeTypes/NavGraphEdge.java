/**
 * @author Petr (http://www.sallyx.org/)
 */
package common.Graph.GraphEdgeTypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class NavGraphEdge extends GraphEdge {

    //examples of typical flags
    final public static int normal = 0;
    final public static int swim = 1 << 0;
    final public static int crawl = 1 << 1;
    final public static int creep = 1 << 3;
    final public static int jump = 1 << 3;
    final public static int fly = 1 << 4;
    final public static int grapple = 1 << 5;
    final public static int goes_through_door = 1 << 6;
    protected int m_iFlags;
    //if this edge intersects with an object (such as a door or lift), then
    //this is that object's ID. 
    protected int m_iIDofIntersectingEntity;

    public NavGraphEdge(int from,
            int to,
            double cost,
            int flags,
            int id) {
        super(from, to, cost);
        m_iFlags = flags;
        m_iIDofIntersectingEntity = id;
    }

    public NavGraphEdge(int from,
            int to,
            double cost, int flags) {
        this(from, to, cost, flags, -1);
    }

    public NavGraphEdge(int from,
            int to,
            double cost) {
        this(from, to, cost, 0);
    }
    
    public NavGraphEdge(NavGraphEdge e) {
        this(e.From(), e.To(), e.Cost(), e.Flags(), e.IDofIntersectingEntity());
    }

    //stream constructor
    public NavGraphEdge(Scanner buffer) throws IOException {
        buffer.next(); //From:
        m_iFrom = buffer.nextInt();
        buffer.next(); //To:
        m_iTo = buffer.nextInt();
        buffer.next(); //Cost:
        m_dCost = buffer.nextDouble();
        buffer.next(); //Flag:
        m_iFlags = buffer.nextInt();
        buffer.next(); //ID:
        m_iIDofIntersectingEntity = buffer.nextInt();
    }

    public int Flags() {
        return m_iFlags;
    }

    public void SetFlags(int flags) {
        m_iFlags = flags;
    }

    public int IDofIntersectingEntity() {
        return m_iIDofIntersectingEntity;
    }

    public void SetIDofIntersectingEntity(int id) {
        m_iIDofIntersectingEntity = id;
    }

    @Override
    public OutputStream print(OutputStream os) {
        PrintStream ps = new PrintStream(os);
        ps.print("m_iFrom: ");
        ps.print(m_iFrom);
        ps.print(" m_iTo: ");
        ps.print(m_iTo);
        ps.print(" m_dCost: ");
        ps.print(m_dCost);
        ps.print(" m_iFlags: ");
        ps.print(m_iFlags);
        ps.print(" ID: ");
        ps.print(m_iIDofIntersectingEntity);
        ps.println();
        return os;
    }
}