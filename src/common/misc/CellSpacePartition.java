package common.misc;

import common.D2.InvertedAABBox2D;
import common.D2.Vector2D;
import static common.D2.Vector2D.add;
import static common.D2.Vector2D.sub;
import static common.D2.Vector2D.Vec2DDistanceSq;
import common.Graph.GraphNodeTypes.NavGraphNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Name: CellSpacePartition.h
 *
 * Author: Mat Buckland (www.ai-junkie.com)
 *
 * Desc: class to divide a 2D space into a grid of cells each of which may
 * contain a number of entities. Once created and initialized with entities,
 * fast proximity querys can be made by calling the CalculateNeighbors method
 * with a position and proximity radius.
 *
 * If an entity is capable of moving, and therefore capable of moving between
 * cells, the Update method should be called each update-cycle to sychronize the
 * entity and the cell space it occupies
 */
/**
 * defines a cell containing a list of pointers to entities
 */
class Cell<entity extends NavGraphNode> {
    //all the entities inhabiting this cell

    List<entity> Members = new LinkedList<entity>();
    //the cell's bounding box (it's inverted because the Window's default
    //co-ordinate system has a y axis that increases as it descends)
    InvertedAABBox2D BBox;

    Cell(Vector2D topleft, Vector2D botright) {
        BBox = new InvertedAABBox2D(topleft, botright);
    }
}

/**
 * the subdivision class
 */
public class CellSpacePartition<entity extends NavGraphNode> {

    /**
     * the required amount of cells in the space
     */
    private List<Cell<entity>> m_Cells = new ArrayList<Cell<entity>>();
    /**
     * this is used to store any valid neighbors when an agent searches its
     * neighboring space
     */
    private List<entity> m_Neighbors;
    /**
     * this iterator will be used by the methods next and begin to traverse
     * through the above vector of neighbors
     */
    private Iterator<entity> m_curNeighbor;
    //the width and height of the world space the entities inhabit
    private double m_dSpaceWidth;
    private double m_dSpaceHeight;
    //the number of cells the space is going to be divided up into
    private int m_iNumCellsX;
    private int m_iNumCellsY;
    private double m_dCellSizeX;
    private double m_dCellSizeY;

    /**
     * Given a 2D vector representing a position within the game world, this
     * method calculates an index into its appropriate cell
     */
    private int PositionToIndex(final Vector2D pos) {
        int idx = (int) (m_iNumCellsX * pos.x / m_dSpaceWidth)
                + ((int) ((m_iNumCellsY) * pos.y / m_dSpaceHeight) * m_iNumCellsX);

        //if the entity's position is equal to vector2d(m_dSpaceWidth, m_dSpaceHeight)
        //then the index will overshoot. We need to check for this and adjust
        if (idx > (int) m_Cells.size() - 1) {
            idx = (int) m_Cells.size() - 1;
        }

        return idx;
    }

//----------------------------- ctor ---------------------------------------
//--------------------------------------------------------------------------
    public CellSpacePartition(double width, //width of 2D space
            double height, //height...
            int cellsX, //number of divisions horizontally
            int cellsY, //and vertically
            int MaxEntitys, //maximum number of entities to partition
            entity eClass) //entity example
    {
        m_dSpaceWidth = width;
        m_dSpaceHeight = height;
        m_iNumCellsX = cellsX;
        m_iNumCellsY = cellsY;
        m_Neighbors = new ArrayList<>(MaxEntitys);
        for (int i = 0; i < MaxEntitys; i++) {
            try {
                entity e = (entity) eClass.getClass().getConstructor().newInstance();
                m_Neighbors.add(e);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        //calculate bounds of each cell
        m_dCellSizeX = width / cellsX;
        m_dCellSizeY = height / cellsY;


        //create the cells
        for (int y = 0; y < m_iNumCellsY; ++y) {
            for (int x = 0; x < m_iNumCellsX; ++x) {
                double left = x * m_dCellSizeX;
                double right = left + m_dCellSizeX;
                double top = y * m_dCellSizeY;
                double bot = top + m_dCellSizeY;

                m_Cells.add(new Cell<entity>(new Vector2D(left, top), new Vector2D(right, bot)));
            }
        }
    }

    /**
     * adds entities to the class by allocating them to the appropriate cell
     */
    public void AddEntity(final entity ent) {
        assert (ent != null);

        int sz = m_Cells.size();
        int idx = PositionToIndex(ent.Pos());

        m_Cells.get(idx).Members.add(ent);
    }

    /**
     * Update an entity's cell by calling this from your entity's Update method.
     * Checks to see if an entity has moved cells. If so the data structure is
     * updated accordingly
     */
    public void UpdateEntity(final entity ent, Vector2D OldPos) {
        //if the index for the old pos and the new pos are not equal then
        //the entity has moved to another cell.
        int OldIdx = PositionToIndex(OldPos);
        int NewIdx = PositionToIndex(ent.Pos());

        if (NewIdx == OldIdx) {
            return;
        }

        //the entity has moved into another cell so delete from current cell
        //and add to new one
        m_Cells.get(OldIdx).Members.remove(ent);
        m_Cells.get(NewIdx).Members.add(ent);
    }

    /**
     * This must be called to create the vector of neighbors.This method
     * examines each cell within range of the target, If the cells contain
     * entities then they are tested to see if they are situated within the
     * target's neighborhood region. If they are they are added to neighbor list
     */
    public void CalculateNeighbors(Vector2D TargetPos, double QueryRadius) {
        //create an iterator and set it to the beginning of the neighbor vector
        //Iterator<entity> curNbor = m_Neighbors.iterator();
        m_Neighbors.clear();

        //create the query box that is the bounding box of the target's query
        //area
        InvertedAABBox2D QueryBox = new InvertedAABBox2D(sub(TargetPos, new Vector2D(QueryRadius, QueryRadius)),
                add(TargetPos, new Vector2D(QueryRadius, QueryRadius)));
        //iterate through each cell and test to see if its bounding box overlaps
        //with the query box. If it does and it also contains entities then
        //make further proximity tests.
        Iterator<Cell<entity>> cellIt = m_Cells.iterator();
        while (cellIt.hasNext()) {
            Cell<entity> curCell = cellIt.next();
            //test to see if this cell contains members and if it overlaps the
            //query box
            if (curCell.BBox.isOverlappedWith(QueryBox)
                    && !curCell.Members.isEmpty()) {
                //add any entities found within query radius to the neighbor list
                Iterator<entity> it = curCell.Members.iterator();
                while (it.hasNext()) {
                    entity e = it.next();
                    if (Vec2DDistanceSq(e.Pos(), TargetPos)
                            < QueryRadius * QueryRadius) {
                        // *curNbor++ = *it;
                        m_Neighbors.add(e);
                    }
                }
            }
        }//next cell

        //mark the end of the list with a zero.
        //*curNbor = 0;
    }

    /**
     * returns a reference to the entity at the front of the neighbor vector
     */
    public entity begin() {
        m_curNeighbor = m_Neighbors.iterator();
        return m_curNeighbor.hasNext() ? m_curNeighbor.next() : null;
    }

    /**
     * this returns the next entity in the neighbor vector
     */
    public entity next() {
        return m_curNeighbor.hasNext() ? m_curNeighbor.next() : null;
    }

    /**
     * returns true if the end of the vector is found (a zero value marks the
     * end)
     */
    public boolean end() {
        return !m_curNeighbor.hasNext();
    }

    /**
     * clears the cells of all entities
     */
    public void EmptyCells() {
        Iterator<Cell<entity>> it = m_Cells.iterator();

        while (it.hasNext()) {
            it.next().Members.clear();
        }
    }

    /**
     * call this to use the gdi to render the cell edges
     */
    public void RenderCells() {
        Iterator<Cell<entity>> curCell = m_Cells.iterator();
        while (curCell.hasNext()) {
            curCell.next().BBox.Render(false);
        }
    }
}