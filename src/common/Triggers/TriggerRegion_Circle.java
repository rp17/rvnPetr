/*
 *  class to define a circular region of influence
 * 
 *  @author Petr (http://www.sallyx.org/)
 */
package common.Triggers;

import common.D2.Vector2D;
import static common.D2.Vector2D.Vec2DDistanceSq;

public class TriggerRegion_Circle extends TriggerRegion {
    //the center of the region

    private Vector2D m_vPos;
    //the radius of the region
    private double m_dRadius;

    public TriggerRegion_Circle(Vector2D pos,
            double radius) {
        m_dRadius = radius;
        m_vPos = new Vector2D(pos);
    }

    @Override
    public boolean isTouching(Vector2D pos, double EntityRadius) {
        return Vec2DDistanceSq(m_vPos, pos) < (EntityRadius + m_dRadius) * (EntityRadius + m_dRadius);
    }
}