/**
 *  Graph node for use in creating a navigation graph. This node contains
 *  the position of the node and a pointer to a BaseGameEntity... useful
 *  if you want your nodes to represent health packs, gold mines and the like
 * 
 * @author Petr (http://www.sallyx.org/)
 */
package common.Graph.GraphNodeTypes;

import common.D2.Vector2D;
import common.Graph.ExtraInfo;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class NavGraphNode<extra_info extends ExtraInfo> extends GraphNode {

    //the node's position
    protected Vector2D m_vPosition = new Vector2D();
    //often you will require a navgraph node to contain additional information.
    //For example a node might represent a pickup such as armor in which
    //case m_ExtraInfo could be an enumerated value denoting the pickup type,
    //thereby enabling a search algorithm to search a graph for specific items.
    //Going one step further, m_ExtraInfo could be a pointer to the instance of
    //the item type the node is twinned with. This would allow a search algorithm
    //to test the status of the pickup during the search. 
    protected extra_info m_ExtraInfo;

    //ctors
    public NavGraphNode() {
        super();
        m_ExtraInfo = extra_info();
    }

    public NavGraphNode(int idx, Vector2D pos) {
        super(idx);
        m_vPosition = new Vector2D(pos);
        m_ExtraInfo = extra_info();
    }

    //stream constructor
    public NavGraphNode(Scanner buffer) {
        m_ExtraInfo = extra_info();
        buffer.next(); //Index:
        m_iIndex = buffer.nextInt();
        buffer.next(); //PosX:
        m_vPosition.x = buffer.nextInt();
        buffer.next(); //PosY:
        m_vPosition.y = buffer.nextInt();
    }
    //for reading and writing to streams.

    @Override
    public OutputStream print(OutputStream os) {
        PrintStream ps = new PrintStream(os);
        ps.print("Index: ");
        ps.print(m_iIndex);
        ps.print(" PosX: ");
        ps.print(m_vPosition.x);
        ps.print(" PosY: ");
        ps.print(m_vPosition.y);
        ps.println();
        return os;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public Vector2D Pos() {
        return new Vector2D(m_vPosition);
    }

    public void SetPos(Vector2D NewPosition) {
        m_vPosition = new Vector2D(NewPosition);
    }

    public extra_info ExtraInfo() {
        return m_ExtraInfo;
    }

    public void SetExtraInfo(extra_info info) {
        m_ExtraInfo = info;
    }

    private extra_info extra_info() {
        try {
            java.lang.reflect.Field  fd= this.getClass().getDeclaredField("m_ExtraInfo");
            return (extra_info) fd.getType().newInstance();
        } catch (InstantiationException ex) {
            return null;
        } catch (IllegalAccessException ex) {
            return null;
        } catch(NoSuchFieldException ex) {
            return null;
        }
    }
}