/*
 *  class to define a circular region of influence
 * 
 *  @author Petr (http://www.sallyx.org/)
 */
package common.Triggers;

import common.D2.InvertedAABBox2D;
import common.D2.Vector2D;

public class TriggerRegion_Rectangle extends TriggerRegion {

    private InvertedAABBox2D m_pTrigger;

    public TriggerRegion_Rectangle(Vector2D TopLeft, Vector2D BottomRight) {
        m_pTrigger = new InvertedAABBox2D(TopLeft, BottomRight);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        m_pTrigger = null;
    }

    /**
     * there's no need to do an accurate (and expensive) circle v rectangle
     * intersection test. Instead we'll just test the bounding box of the given
     * circle with the rectangle.
     */
    public boolean isTouching(Vector2D pos, double EntityRadius) {
        InvertedAABBox2D Box = new InvertedAABBox2D(new Vector2D(pos.x - EntityRadius, pos.y - EntityRadius),
                new Vector2D(pos.x + EntityRadius, pos.y + EntityRadius));

        return Box.isOverlappedWith(m_pTrigger);
    }
}