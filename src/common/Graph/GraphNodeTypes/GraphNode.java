/**
 * @author Petr (http://www.sallyx.org/)
 */
package common.Graph.GraphNodeTypes;

import java.io.PrintStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.io.InputStream;
import static common.Graph.NodeTypeEnumerations.invalid_node_index;

public class GraphNode {
    //every node has an index. A valid index is >= 0

    protected int m_iIndex;

    public GraphNode() {
        m_iIndex = invalid_node_index;
    }

    public GraphNode(int idx) {
        m_iIndex = idx;
    }

    public GraphNode(InputStream stream) {
        Scanner buffer = new Scanner(stream);
        buffer.next();
        m_iIndex = buffer.nextInt();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public int Index() {
        return m_iIndex;
    }

    public void SetIndex(int NewIndex) {
        m_iIndex = NewIndex;
    }

    //for reading and writing to streams.
    public OutputStream print(OutputStream os) {
        PrintStream ps = new PrintStream(os);
        ps.print("Index: ");
        ps.print(m_iIndex);
        ps.println();
        return os;
    }
}
